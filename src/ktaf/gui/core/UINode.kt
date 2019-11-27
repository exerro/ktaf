package ktaf.gui.core

import geometry.vec2
import geometry.vec2_zero
import ktaf.data.animation.Animation
import ktaf.data.animation.EasingFunctions
import ktaf.data.property.AnimatedProperty
import ktaf.data.property.mutableProperty
import ktaf.graphics.DrawContext2D
import lwjglkt.glfw.*
import observables.UnitSubscribable
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/** A node in the GUI tree. */
abstract class UINode {
    /** Specific width for the node. Leave as `null` for automatic sizing. */
    val width = mutableProperty<Float?>(null)
    /** Specific height for the node. Leave as `null` for automatic sizing. */
    val height = mutableProperty<Float?>(null)
    /** Padding around the content of the node. */
    val padding = paddingProperty()
    /** Whether the node should expand to fit space available or contract to fit
     *  its content. */
    val expand = mutableProperty(true)
    /** The cursor for the node. */
    val cursor = mutableProperty(GLFWCursor.DEFAULT)

    ////////////////////////////////////////////////////////////////////////////

    /** Indicate that this node should expand to fill any space available, and
     *  not use its default size. */
    fun expand() { expand.value = true }

    /** Indicate that this node should fall back to its default size where
     *  possible, and not expand to fill any space available. */
    fun contract() { expand.value = false }

    ////////////////////////////////////////////////////////////////////////////

    /** Calculated position of the node, relative to (0, 0), calculated
     *  automatically. */
    var calculatedPosition: vec2 = vec2_zero
        private set

    /** Calculated size of the node, computed automatically. */
    var calculatedSize: vec2 = vec2_zero
        private set

    /** The node's current position, relative to (0, 0), updated automatically. */
    var position: vec2 = vec2_zero
        private set

    /** The node's current size, updated automatically. */
    var size: vec2 = vec2_zero
        private set

    ////////////////////////////////////////////////////////////////////////////

    /** Initialise the node, given the draw context it will be using. */
    @Deprecated("This way of doing things is utterly stupid (think child added/removed)")
    open fun initialise(drawContext: DrawContext2D) {}

    /** Called when the mouse enters the node. */
    open fun entered() {}

    /** Called when the mouse exits the node. */
    open fun exited() {}

    /** Return a node to handle mouse events at the given position. */
    open fun getMouseHandler(position: CursorPosition): UINode?
            = this.takeIf { contains(position) }

    /** Return a node to handle the key event. */
    open fun getKeyHandler(event: KeyEvent): UINode? = null

    /** Return a node to handle text input. */
    open fun getInputHandler(): UINode? = null

    /** Handle a mouse event. */
    open fun handleMouseEvent(event: MouseEvent) {}

    /** Handle a key event */
    open fun handleKeyEvent(event: KeyEvent) {}

    /** Handle a text input event. */
    open fun handleInput(event: TextInputEvent) {}

    /** Return an optional width the node should fall to if not filling its
     *  parent and with no fixed width set.
     *
     *  Called from calculateWidth() if necessary.
     *  Note: for parents, calculateChildrenWidths() will already have been
     *        called, so child widths can be used here. */
    abstract fun getDefaultWidth(): Float?

    /** Return an optional height the node should fall to if not filling its
     *  parent and with no fixed height set.
     *
     *  @param width: calculated width of the node.
     *
     *  Called from calculateHeight() if necessary.
     *  Note: for parents, calculateChildrenHeights() will already have been
     *        called, so child sizes can be used here. */
    abstract fun getDefaultHeight(width: Float): Float?

    /** Draw the node. */
    abstract fun draw(context: DrawContext2D)

    /** Update the node and its animations. */
    open fun update(dt: Float) {
        positionAnimation?.update(dt)
        positionAnimation?.let { position = it.current }
        if (positionAnimation?.finished() == true) positionAnimation = null

        sizeAnimation?.update(dt)
        sizeAnimation?.let { size = it.current }
        if (sizeAnimation?.finished() == true) sizeAnimation = null

        animatedProperties.forEach { it.update(dt) }
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Register a property to be updated when the node updates.
     *
     *  Note: public properties defined on the node are automatically added. */
    protected fun addAnimatedProperty(property: AnimatedProperty<*>) {
        animatedProperties.add(property)
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Position the element at the given absolute location.
     *
     *  Note that the width and height of the node will already have been
     *  calculated before this function is called. */
    internal open fun position(position: vec2) {
        calculatedPosition = position
        updatePositionAndSizeAnimations()
    }

    /** Calculate widths associated with this element
     *
     *  Note: overridden only by UIParent. */
    internal open fun calculateWidth(availableWidth: Float) {
        calculatedWidth = width.value
                ?: if (expand.value) availableWidth
                else getDefaultWidth() ?: availableWidth
    }

    /** Calculate heights associated with this element.
     *
     *  Note: overridden only by UIParent. */
    internal open fun calculateHeight(availableHeight: Float?) {
        val calculatedHeight = height.value
                ?: if (expand.value) availableHeight ?: getDefaultHeight(calculatedWidth) ?: 0f
                else getDefaultHeight(calculatedWidth) ?: availableHeight ?: 0f

        calculatedSize = vec2(calculatedWidth, calculatedHeight)
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun updatePositionAndSizeAnimations() {
        if (positioned) {
            positionAnimation = updateAnimation(positionAnimation, position, calculatedPosition)
            sizeAnimation = updateAnimation(sizeAnimation, size, calculatedSize)
        }
        else {
            enter()
        }
    }

    private fun enter() {
        val (p0, p1) = entrance.position(calculatedPosition, calculatedSize)
        val (s0, s1) = entrance.size(calculatedPosition, calculatedSize)
        position = p0
        size = s0
        positionAnimation = updateAnimation(null, p0, p1)
        sizeAnimation = updateAnimation(null, s0, s1)
        positioned = true
    }

    private fun updateAnimation(animation: Animation<vec2>?, current: vec2, target: vec2): Animation<vec2>? {
        if (animation?.end == target) return animation
        if (current == target) return null
        return Animation(current, target, ANIMATION_DURATION, ANIMATION_EASING) { a, b, t ->
            a * (1 - t) + b * t
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val animatedProperties by lazy {
        this::class.memberProperties
                .asSequence()
                .map { it as KProperty1<UINode, *> }
                .filter { it.visibility == KVisibility.PUBLIC }
                .map { it.get(this) }
                .filterIsInstance<AnimatedProperty<*>>()
                .toMutableList()
    }

    internal var positioned = false

    private var calculatedWidth: Float = 0f
    private var positionAnimation: Animation<vec2>? = null
    private var sizeAnimation: Animation<vec2>? = null

    // TODO: make these properties
    private val entrance = Entrance.GROW
    private val ANIMATION_DURATION = 0.3f
    private val ANIMATION_EASING = EasingFunctions.smooth

    ////////////////////////////////////////////////////////////////////////////

    fun contains(cursor: CursorPosition)
            = cursor.x >= position.x && cursor.y >= position.y &&
              cursor.x < position.x + size.x && cursor.y < position.y + size.y
}
