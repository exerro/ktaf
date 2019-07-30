package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.layout.*

open class UIContainer(colour: RGBA = rgba(1f, 0f)): UINode() {
    // structure
    val children = KTAFList<UINode>()
    val ordering = KTAFValue(Ordering())
    val colour = UIAnimatedProperty(colour, this, "colour")

    // configuration
    val layout = KTAFValue<UILayout>(FillLayout())

    /**
     * TODO: comment
     */
    open fun computePositionForChildren() {
        val node = this

        layout.get().position(
                node.computedWidth - node.padding.get().width,
                node.computedHeight - node.padding.get().height
        )
    }

    // update children
    override fun update(event: UpdateEvent) {
        super.update(event)
        children.forEach { it.update(event) }
    }

    // draw children
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        fillBackground(context, position, size, colour.get())
        drawChildren(children, context, position)
    }

    // check children for input handlers before deferring to super
    override fun getMouseHandler(position: vec2): UINode?
            = children.takeIf { position.x >= 0 && position.y >= 0 && position.x <= currentComputedWidth.get() && position.y <= currentComputedHeight.get() }
            ?.reversed() ?.firstNotNull { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
            ?: super.getMouseHandler(position)

    // check children for input handlers before deferring to super
    override fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            =  children.reversed().firstNotNull { it.getKeyboardHandler(key, modifiers) }
            ?: super.getKeyboardHandler(key, modifiers)

    // check children for input handlers before deferring to super
    override fun getInputHandler(): UINode?
            =  children.reversed().firstNotNull { it.getInputHandler() }
            ?: super.getInputHandler()

    // compute the width of children prior to calling the super method
    override fun computeWidth(widthAllocated: Float?) {
        // the width allocated for children
        val widthAllocatedInnerPlusPadding = (width.get() ?: widthAllocated ?.let { it - margin.get().width })
        // compute the width of each child
        layout.get().computeChildrenWidths(widthAllocatedInnerPlusPadding ?.let { it - padding.get().width })
        // update the node's width
        super.computeWidth(widthAllocated)
    }

    // compute the height of children prior to calling the super method
    override fun computeHeight(heightAllocated: Float?) {
        // the height allocated for the children (ignoring subtracting the node's padding)
        val heightAllocatedInnerPlusPadding = (height.get() ?: heightAllocated ?.let { it - margin.get().height })
        // compute the height of each child
        layout.get().computeChildrenHeights(
                computedWidth - padding.get().width,
                heightAllocatedInnerPlusPadding ?.let { it - padding.get().height }
        )
        // update the node's height
        super.computeHeight(heightAllocated)
    }

    // return the width of the children plus padding, based on the layout's rules
    override fun computeContentWidth(width: Float?)
            = layout.get().computeChildrenWidth() + padding.get().width

    // return the height of the children plus padding, based on the layout's rules
    override fun computeContentHeight(width: Float, height: Float?)
            = layout.get().computeChildrenHeight() + padding.get().height

    init {
        propertyState(this.colour)

        children.connectAdded { child ->
            child.parent.set(this)
            child.scene.set(scene.get())
        }

        children.connectRemoved { child ->
            child.parent.set(null)
            child.scene.set(null)
        }

        scene.connect { scene -> children.forEach { it.scene.set(scene) } }

        fill(true)
    }
}
