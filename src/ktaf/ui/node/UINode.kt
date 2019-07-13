package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.*
import ktaf.ui.graphics.Background
import ktaf.ui.graphics.Foreground
import ktaf.ui.layout.*
import ktaf.ui.scene.UIScene
import ktaf.util.animate
import lwjglkt.GLFWCursor
import kotlin.properties.Delegates

abstract class UINode {
    // structure
    val children = KTAFList<UINode>()
    var ordering = KTAFValue(Ordering())
    val scene = KTAFValue<UIScene?>(null)
    val parent = KTAFValue<UINode?>(null)

    // configuration
    val width = UIProperty<Float?>(null)
    val height = UIProperty<Float?>(null)
    val margin = UIProperty(Border(0f))
    val padding = UIProperty(Border(0f))
    val layout = KTAFValue<UILayout>(FillLayout())
    val hotkeys = KTAFList<Hotkey>()

    // state
    val state = KTAFValue(listOf<UINodeState>())
    val computedX = KTAFValue(0f)
    val computedY = KTAFValue(0f)
    val computedWidth = KTAFValue(0f)
    val computedHeight = KTAFValue(0f)
    val computedPosition = KTAFValue(vec2(0f))

    // callbacks
    val onFocus = EventHandlerList<UIFocusEvent>()
    val onUnFocus = EventHandlerList<UIUnFocusEvent>()
    val onMouseEvent = EventHandlerList<UIMouseEvent>()
    val onMouseButtonEvent = EventHandlerList<UIMouseButtonEvent>()
    val onMouseEnter = EventHandlerList<UIMouseEnterEvent>()
    val onMouseExit = EventHandlerList<UIMouseExitEvent>()
    val onMousePress = EventHandlerList<UIMousePressEvent>()
    val onMouseRelease = EventHandlerList<UIMouseReleaseEvent>()
    val onMouseClick = EventHandlerList<UIMouseClickEvent>()
    val onMouseMove = EventHandlerList<UIMouseMoveEvent>()
    val onMouseDrag = EventHandlerList<UIMouseDragEvent>()
    val onKeyEvent = EventHandlerList<UIKeyEvent>()
    val onKeyPress = EventHandlerList<UIKeyPressEvent>()
    val onKeyRelease = EventHandlerList<UIKeyReleaseEvent>()
    val onTextInput = EventHandlerList<UITextInputEvent>()

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

    /** Return true if this node can handle the key event */
    open fun handlesKey(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): Boolean
            = hotkeys.any { it.matches(key, modifiers) }

    /** Return true if this node can handle input */
    open fun handlesInput(): Boolean
            = false

    open fun getMouseHandler(position: vec2): UINode?
            =  children.reversed().firstNotNull { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
            ?: this.takeIf { position.x >= 0 && position.y >= 0 && position.x < computedWidth.get() && position.y < computedHeight.get() }

    open fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            = children.reversed().firstNotNull { it.getKeyboardHandler(key, modifiers) } ?: this.takeIf { handlesKey(key, modifiers) }

    open fun getInputHandler(): UINode?
            = children.reversed().firstNotNull { it.getInputHandler() } ?: this.takeIf { handlesInput() }

    protected fun <T> propertyState(property: UIProperty<T>) {
        state.connect { property.setState(state.current()) }
    }

    init {
        propertyState(width)
        propertyState(height)
        propertyState(margin)
        propertyState(padding)

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

    // configuration internals
    internal val foregroundsInternal = mutableListOf<Foreground>()
    internal val backgroundsInternal = mutableListOf<Background>()
    internal open var fillSize = true // whether the node should fill allocated size when positioning
    internal open val cursor: GLFWCursor? = GLFWCursor.DEFAULT

    // state
    internal var computedXInternal: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animations?.animate(this, ::computedX, new) }
    }
    internal var computedYInternal: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animations?.animate(this, ::computedY, new) }
    }
    internal var computedWidthInternal: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animations?.animate(this, ::computedWidth, new) }
    }
    internal var computedHeightInternal: Float by Delegates.observable(0f) { _, old, new ->
        if (old != new) { scene.get()?.animations?.animate(this, ::computedHeight, new) }
    }
}

fun UINode.handleEvent(event: UIEvent) {
    when (event) {
        is UIKeyEvent -> onKeyEvent.trigger(event)
        is UIMouseEvent -> onMouseEvent.trigger(event)
    }

    when (event) {
        is UIMouseButtonEvent -> onMouseButtonEvent.trigger(event)
    }

    when (event) {
        is UIMouseEnterEvent -> onMouseEnter.trigger(event)
        is UIMouseExitEvent -> onMouseExit.trigger(event)
        is UIMouseMoveEvent -> onMouseMove.trigger(event)
        is UIMouseDragEvent -> onMouseDrag.trigger(event)
        is UIMousePressEvent -> onMousePress.trigger(event)
        is UIMouseReleaseEvent -> onMouseRelease.trigger(event)
        is UIMouseClickEvent -> onMouseClick.trigger(event)
        is UIKeyPressEvent -> onKeyPress.trigger(event)
        is UIKeyReleaseEvent -> onKeyRelease.trigger(event)
        is UITextInputEvent -> onTextInput.trigger(event)
        is UIFocusEvent -> onFocus.trigger(event)
        is UIUnFocusEvent -> onUnFocus.trigger(event)
    }
}

fun <T, R> List<T>.firstNotNull(fn: (T) -> R?): R? {
    for (x in this) fn(x)?.let { return it }
    return null
}
