package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.*
import ktaf.ui.graphics.Background
import ktaf.ui.graphics.Foreground
import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.UILayout
import ktaf.ui.scene.UIScene
import ktaf.ui.scene.animate
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
    val computedPosition = KTAFMutableValue(vec2(0f))

    open fun computeHeight(width: Float) = height.get()

    open fun update(dt: Float) {}

    open fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        backgroundsInternal.reversed().forEach {
            it.draw(context, position, size)
        }

        children.forEach {
            it.draw(context,
                    position + padding.get().tl + it.computedPosition.get(),
                    vec2(it.computedWidth.get(), it.computedHeight.get())
            )
        }

        foregroundsInternal.forEach {
            it.draw(context, position + padding.get().tl, size - padding.get().size)
        }
    }

    open fun getMouseHandler(position: vec2): UINode?
            =  children.reversed().firstNotNull { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
            ?: this.takeIf { position.x >= 0 && position.y >= 0 && position.x < computedWidth.get() && position.y < computedHeight.get() }

    open fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            = children.reversed().firstNotNull { it.getKeyboardHandler(key, modifiers) }

    open fun getInputHandler(): UINode?
            = children.reversed().firstNotNull { it.getInputHandler() }

    open fun handleEvent(event: UIEvent) {
        when (event) {
            is UIKeyEvent -> handleKeyEvent(event)
            is UIMouseEvent -> handleMouseEvent(event)
            is UITextInputEvent -> onTextInput.trigger(event)
            is UIFocusEvent -> onFocus.trigger(event)
            is UIUnFocusEvent -> onUnFocus.trigger(event)
        }
    }

    open fun handleKeyEvent(event: UIKeyEvent) {
        onKeyEvent.trigger(event)

        when (event) {
            is UIKeyPressEvent -> onKeyPress.trigger(event)
            is UIKeyReleaseEvent -> onKeyRelease.trigger(event)
        }
    }

    open fun handleMouseButtonEvent(event: UIMouseButtonEvent) {
        onMouseButtonEvent.trigger(event)

        when (event) {
            is UIMousePressEvent -> onMousePress.trigger(event)
            is UIMouseReleaseEvent -> onMouseRelease.trigger(event)
            is UIMouseClickEvent -> onMouseClick.trigger(event)
        }
    }

    open fun handleMouseEvent(event: UIMouseEvent) {
        onMouseEvent.trigger(event)

        when (event) {
            is UIMouseButtonEvent -> handleMouseButtonEvent(event)
            is UIMouseEnterEvent -> onMouseEnter.trigger(event)
            is UIMouseExitEvent -> onMouseExit.trigger(event)
            is UIMouseMoveEvent -> onMouseMove.trigger(event)
            is UIMouseDragEvent -> onMouseDrag.trigger(event)
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

        computedX.connect { computedPosition.set(vec2(it, computedY.get())) }
        computedY.connect { computedPosition.set(vec2(computedX.get(), it)) }

        parent.connectComparator { old, new ->
            old?.children?.remove(this)
            if (new?.children?.contains(this) != true) new?.children?.add(this)
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
    internal val onFocus = EventHandlerList<UIFocusEvent>()
    internal val onUnFocus = EventHandlerList<UIUnFocusEvent>()
    internal val onMouseEvent = EventHandlerList<UIMouseEvent>()
    internal val onMouseButtonEvent = EventHandlerList<UIMouseButtonEvent>()
    internal val onMouseEnter = EventHandlerList<UIMouseEnterEvent>()
    internal val onMouseExit = EventHandlerList<UIMouseExitEvent>()
    internal val onMousePress = EventHandlerList<UIMousePressEvent>()
    internal val onMouseRelease = EventHandlerList<UIMouseReleaseEvent>()
    internal val onMouseClick = EventHandlerList<UIMouseClickEvent>()
    internal val onMouseMove = EventHandlerList<UIMouseMoveEvent>()
    internal val onMouseDrag = EventHandlerList<UIMouseDragEvent>()
    internal val onKeyEvent = EventHandlerList<UIKeyEvent>()
    internal val onKeyPress = EventHandlerList<UIKeyPressEvent>()
    internal val onKeyRelease = EventHandlerList<UIKeyReleaseEvent>()
    internal val onTextInput = EventHandlerList<UITextInputEvent>()

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

private fun <T, R> List<T>.firstNotNull(fn: (T) -> R?): R? {
    for (x in this) fn(x)?.let { return it }
    return null
}
