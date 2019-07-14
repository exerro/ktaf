package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.ui.layout.*
import lwjglkt.GLFWCursor

open class UIContainer: UINode() {
    // structure
    val children = KTAFList<UINode>()
    var ordering = KTAFValue(Ordering())

    // configuration
    val layout = KTAFValue<UILayout>(FillLayout())

    /**
     * TODO
     */
    open fun computePositionForChildren() {
        val node = this

        layout.get().position(
                node.computedWidthInternal - node.padding.get().width,
                node.computedHeightInternal - node.padding.get().height
        )
    }

    // update children
    override fun update(dt: Float) {
        super.update(dt)
        children.forEach { it.update(dt) }
    }

    // draw children
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        drawChildren(children, context, position)
    }

    // check children for input handlers before deferring to super
    override fun getMouseHandler(position: vec2): UINode?
            =  children.reversed().firstNotNull { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
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
                computedWidthInternal - padding.get().width,
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
