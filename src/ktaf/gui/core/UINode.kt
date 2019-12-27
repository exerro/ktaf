package ktaf.gui.core

import geometry.vec2
import geometry.vec2_zero
import ktaf.data.animation.Animation
import ktaf.data.animation.EasingFunctions
import ktaf.data.property.AnimatedProperty
import ktaf.data.property.mutableProperty
import ktaf.graphics.DrawContext2D
import lwjglkt.glfw.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/** A node in the GUI tree. */
abstract class UINode: UINodeEvents, UINodePositioning {
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
    val cursor = mutableProperty(GLFWCursor.DEFAULT) // TODO: this shouldn't be a property

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
        internal set

    /** The node's current size, updated automatically. */
    var size: vec2 = vec2_zero
        internal set

    var parented: Boolean = false
        internal set

    ////////////////////////////////////////////////////////////////////////////

    /** Return a node to handle mouse events at the given position. */
    open fun getMouseHandler(position: CursorPosition): UINode?
            = this.takeIf { contains(position) }

    /** Return a node to handle the key event. */
    open fun getKeyHandler(event: KeyEvent): UINode? = null

    /** Return a node to handle text input. */
    open fun getInputHandler(): UINode? = null

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

    /** The draw context of the scene or parent this node is a child of. */
    protected lateinit var drawContext: DrawContext2D
        private set

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

    internal open fun setDrawContext(context: DrawContext2D) {
        drawContext = context
    }

    internal fun exitTo(position: vec2, size: vec2) {
        calculatedPosition = position
        calculatedSize = size
        updatePositionAndSizeAnimations()
    }

    internal fun hasDrawContext() = ::drawContext.isInitialized

    internal var positioned = false
    internal val animating get() = positionAnimation != null || sizeAnimation != null

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
        val p0 = entrance.position(calculatedPosition, calculatedSize)
        val s0 = entrance.size(calculatedPosition, calculatedSize)
        position = p0
        size = s0
        positionAnimation = updateAnimation(null, p0, calculatedPosition)
        sizeAnimation = updateAnimation(null, s0, calculatedSize)
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

    private var calculatedWidth: Float = 0f
    private var positionAnimation: Animation<vec2>? = null
    private var sizeAnimation: Animation<vec2>? = null

    // TODO: make these properties
    private val entrance = Entrance.grow // Entrance.fromLeft()
    internal val exit = Exit.shrink // Exit.toTop()
    private val ANIMATION_DURATION = 0.3f
    private val ANIMATION_EASING = EasingFunctions.smooth

    ////////////////////////////////////////////////////////////////////////////

    fun contains(cursor: CursorPosition)
            = cursor.x >= position.x && cursor.y >= position.y &&
              cursor.x < position.x + size.x && cursor.y < position.y + size.y
}
