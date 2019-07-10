package ktaf.ui

import ktaf.KTAFMutableList
import ktaf.KTAFMutableValue
import ktaf.graphics.DrawContext2D
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.UILayout
import lwjglkt.GLFWCursor
import kotlin.properties.Delegates

abstract class UINode {
    // structure
    val children = KTAFMutableList<UINode>()
    val scene = KTAFMutableValue<UIScene?>(null)
    val parent = KTAFMutableValue<UINode?>(null)

    // configuration
    val width = UIProperty<Float?>(null)
    val height = UIProperty<Float?>(null)
    val margin = UIProperty(Border(0f))
    val padding = UIProperty(Border(0f))
    val layout = KTAFMutableValue<UILayout>(ListLayout())

    // state
    val state = KTAFMutableValue(DEFAULT_STATE)
    val computedX = KTAFMutableValue(0f)
    val computedY = KTAFMutableValue(0f)
    val computedWidth = KTAFMutableValue(0f)
    val computedHeight = KTAFMutableValue(0f)

    open fun computeHeight(width: Float) = height.get()

    open fun update(dt: Float) {}

    open fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        backgroundsInternal.reversed().forEach {
            it.draw(context, position, size)
        }

        children.forEach {
            it.draw(context,
                    position + padding.get().tl + vec2(it.computedX.get(), it.computedY.get()),
                    vec2(it.computedWidth.get(), it.computedHeight.get())
            )
        }

        foregroundsInternal.forEach {
            it.draw(context, position + padding.get().tl, size - padding.get().size)
        }
    }

    open fun handleEvent(event: UIEvent) {
        when (event) {
            is UIKeyEvent -> handleKeyEvent(event)
            is UIMouseEvent -> handleMouseEvent(event)
            is UITextInputEvent -> textInputEventHandlers.forEach { it(event) }
            is UIFocusEvent -> focusEventHandlers.forEach { it(event) }
            is UIUnFocusEvent -> unFocusEventHandlers.forEach { it(event) }
        }

        when (event) {
            is UITextInputEvent, is UIFocusEvent, is UIUnFocusEvent -> children.forEach { it.handleEvent(event) }
            else -> { /* do nothing */ }
        }
    }

    open fun handleKeyEvent(event: UIKeyEvent) {
        keyEventHandlers.forEach { it(event) }

        when (event) {
            is UIKeyPressEvent -> keyPressEventHandlers.forEach { it(event) }
            is UIKeyReleaseEvent -> keyReleaseEventHandlers.forEach { it(event) }
        }

        children.forEach { it.handleEvent(event) }
    }

    open fun handleMouseEvent(event: UIMouseEvent) {
        mouseEventHandlers.forEach { it(event) }

        when (event) {
            is UIMouseEnterEvent -> mouseEnterEventHandlers.forEach { it(event) }
            is UIMouseExitEvent -> mouseExitEventHandlers.forEach { it(event) }
            is UIMousePressEvent -> mousePressEventHandlers.forEach { it(event) }
            is UIMouseReleaseEvent -> mouseReleaseEventHandlers.forEach { it(event) }
            is UIMouseClickEvent -> mouseClickEventHandlers.forEach { it(event) }
            is UIMouseMoveEvent -> mouseMoveEventHandlers.forEach { it(event) }
            is UIMouseDragEvent -> mouseDragEventHandlers.forEach { it(event) }
        }

        children.reversed().forEach {
            it.handleEvent(event.relativeTo(padding.get().tl + vec2(it.computedX.get(), it.computedY.get())))
        }
    }

    init {
        state.connect(width::setState)
        state.connect(height::setState)
        state.connect(margin::setState)
        state.connect(padding::setState)

        scene.connect { scene ->
            children.forEach { it.scene.set(scene) }
        }

        parent.connectComparator { old, new ->
            old?.children?.remove(this)
            new?.children?.add(this)
            Unit
        }

        children.connectAdded { child ->
            child.parent.set(this)
            child.scene.set(scene.get())
        }

        children.connectRemoved { child ->
            child.parent.set(null)
            child.scene.set(null)
        }
    }

    // callbacks
    internal val focusEventHandlers: EventHandlerList<UIFocusEvent> = mutableListOf()
    internal val unFocusEventHandlers: EventHandlerList<UIUnFocusEvent> = mutableListOf()
    internal val mouseEventHandlers: EventHandlerList<UIMouseEvent> = mutableListOf()
    internal val mouseEnterEventHandlers: EventHandlerList<UIMouseEnterEvent> = mutableListOf()
    internal val mouseExitEventHandlers: EventHandlerList<UIMouseExitEvent> = mutableListOf()
    internal val mousePressEventHandlers: EventHandlerList<UIMousePressEvent> = mutableListOf()
    internal val mouseReleaseEventHandlers: EventHandlerList<UIMouseReleaseEvent> = mutableListOf()
    internal val mouseClickEventHandlers: EventHandlerList<UIMouseClickEvent> = mutableListOf()
    internal val mouseMoveEventHandlers: EventHandlerList<UIMouseMoveEvent> = mutableListOf()
    internal val mouseDragEventHandlers: EventHandlerList<UIMouseDragEvent> = mutableListOf()
    internal val keyEventHandlers: EventHandlerList<UIKeyEvent> = mutableListOf()
    internal val keyPressEventHandlers: EventHandlerList<UIKeyPressEvent> = mutableListOf()
    internal val keyReleaseEventHandlers: EventHandlerList<UIKeyReleaseEvent> = mutableListOf()
    internal val textInputEventHandlers: EventHandlerList<UITextInputEvent> = mutableListOf()

    // configuration internals
    internal val foregroundsInternal = mutableListOf<Foreground>()
    internal val backgroundsInternal = mutableListOf<Background>()
    internal open var fillAllocatedSize = true
    internal open val cursor: GLFWCursor? = GLFWCursor.DEFAULT

    // state
    internal var mouseInside = true
    internal var computedXCachedSetter: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animate(this, ::computedX, new) }
    }
    internal var computedYCachedSetter: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animate(this, ::computedY, new) }
    }
    internal var computedWidthCachedSetter: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animate(this, ::computedWidth, new) }
    }
    internal var computedHeightCachedSetter: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animate(this, ::computedHeight, new) }
    }
}
