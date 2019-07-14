package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.ui.*
import ktaf.ui.layout.Border
import ktaf.ui.scene.UIScene
import ktaf.util.animate
import lwjglkt.GLFWCursor
import kotlin.properties.Delegates

abstract class UINode {
    // structure
    val scene = KTAFValue<UIScene?>(null)
    val parent = KTAFValue<UIContainer?>(null)

    // configuration
    val width = UIProperty<Float?>(null)
    val height = UIProperty<Float?>(null)
    val margin = UIProperty(Border(0f))
    val padding = UIProperty(Border(0f))
    val hotkeys = KTAFList<Hotkey>() // TODO: comment
    val fill = KTAFValue(true) // TODO: comment

    // state
    val state = KTAFValue(listOf<UINodeState>()) // TODO: comment
    val focused = KTAFValue(false) // TODO: comment
    val computedX = KTAFValue(0f)
    val computedY = KTAFValue(0f)
    val computedWidth = KTAFValue(0f)
    val computedHeight = KTAFValue(0f)
    val computedPosition = KTAFValue(vec2(0f))

    // callbacks
    val onFocus = EventHandlerList<UIFocusEvent>()
    val onUnFocus = EventHandlerList<UIUnFocusEvent>()
    val onMouseEvent = EventHandlerList<MouseEvent>()
    val onMouseButtonEvent = EventHandlerList<MouseButtonEvent>()
    val onMouseEnter = EventHandlerList<UIMouseEnterEvent>()
    val onMouseExit = EventHandlerList<UIMouseExitEvent>()
    val onMousePress = EventHandlerList<MousePressEvent>()
    val onMouseRelease = EventHandlerList<MouseReleaseEvent>()
    val onMouseClick = EventHandlerList<MouseClickEvent>()
    val onMouseMove = EventHandlerList<MouseMoveEvent>()
    val onMouseDrag = EventHandlerList<MouseDragEvent>()
    val onKeyEvent = EventHandlerList<KeyEvent>()
    val onKeyPress = EventHandlerList<KeyPressEvent>()
    val onKeyRelease = EventHandlerList<KeyReleaseEvent>()
    val onTextInput = EventHandlerList<TextInputEvent>()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Update the node */
    open fun update(event: UpdateEvent) {}

    /** Draw the node */
    abstract fun draw(context: DrawContext2D, position: vec2, size: vec2)

    /** Return the cursor to show when hovering over this node */
    open fun cursor(): GLFWCursor? = GLFWCursor.DEFAULT

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the width of the node based on its set width and the computed width of its contents
     * @param widthAllocated the width allocated for the node
     */
    open fun computeWidth(widthAllocated: Float?) {
        // TODO: comment
        computedWidthInternal = width.get()
                ?: widthAllocated.takeIf { fill.get() } ?: computeContentWidth(widthAllocated)
    }

    /**
     * Sets the height of the node based on its set height and the computed height of its contents
     * @param heightAllocated the height allocated for the node, or null if no height was allocated
     */
    open fun computeHeight(heightAllocated: Float?) {
        // TODO: comment
        computedHeightInternal = height.get()
                ?: heightAllocated.takeIf { fill.get() } ?: computeContentHeight(computedWidthInternal, heightAllocated)
    }

    /** Return the width of the content of this node */
    abstract fun computeContentWidth(width: Float?): Float

    /** Return the height of the content of this node */
    abstract fun computeContentHeight(width: Float, height: Float?): Float

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Return the mouse event handler for an event at the given location */
    open fun getMouseHandler(position: vec2): UINode?
            = this.takeIf { position.x >= 0 && position.y >= 0 && position.x < computedWidth.get() && position.y < computedHeight.get() }

    /** Return the keyboard event handler for an event */
    open fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            = this.takeIf { handlesKey(key, modifiers) }

    /** Return the input event handler for an event */
    open fun getInputHandler(): UINode?
            = this.takeIf { handlesInput() }

    /** Return true if this node can handle the key event */
    open fun handlesKey(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): Boolean
            = hotkeys.any { it.matches(key, modifiers) }

    /** Return true if this node can handle input */
    open fun handlesInput(): Boolean
            = false

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        propertyState(width)
        propertyState(height)
        propertyState(margin)
        propertyState(padding)

// TODO:
//        this.javaClass.declaredFields
//                .map { it.kotlinProperty }
//                .filter { it?.isAccessible ?: false }
//                .mapNotNull { it?.getter?.call(this) }
//                .map { println(it) }
//                .filterIsInstance<UIProperty<*>>()
//                .forEach { propertyState(it) }

        focused.connect { if (it) scene.get()?.focussedNode?.set(this) }
        computedX.connect { computedPosition.set(vec2(it, computedY.get())) }
        computedY.connect { computedPosition.set(vec2(computedX.get(), it)) }

        parent.connectComparator { old, new ->
            old?.children?.remove(this)
            if (new?.children?.contains(this) != true) new?.children?.add(this)
        }

        onMouseEnter { state.push(HOVER) }
        onMouseExit { state.remove(HOVER) }
    }

    /** Register a property as a state-dependent property, making state changes to update this property */
    protected fun <T> propertyState(property: UIProperty<T>) {
        state.connect { property.setState(state.current()) }
    }

    // computed position helpers
    internal var computedXInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) {
        scene.get()?.animations?.animate(this, ::computedX, new) ?: run { computedX(new) }
    } }
    internal var computedYInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) {
        scene.get()?.animations?.animate(this, ::computedY, new) ?: run { computedY(new) }
    } }
    internal var computedWidthInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) {
        scene.get()?.animations?.animate(this, ::computedWidth, new) ?: run { computedWidth(new) }
    } }
    internal var computedHeightInternal: Float by Delegates.observable(0f) { _, old, new -> if (old != new) {
        scene.get()?.animations?.animate(this, ::computedHeight, new) ?: run { computedHeight(new) }
    } }

    companion object {
        const val HOVER = "hover"
    }
}

// TODO: move to utils
fun <T, R> List<T>.firstNotNull(fn: (T) -> R?): R? {
    for (x in this) fn(x)?.let { return it }
    return null
}
