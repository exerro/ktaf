package ktaf.ui.node

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.layout.*
import lwjglkt.GLFWCursor
import kotlin.math.max

open class UIContainer: UINode() {
    // structure
    val children = KTAFList<UINode>()
    var ordering = KTAFValue(Ordering())

    // configuration
    val layout = KTAFValue<UILayout>(FillLayout())

    /**
     * Sets the width of a node based on the width of its children and the node's positioning protocols
     * @param widthAllocated the width allocated for the node
     */
    override fun computeWidth(widthAllocated: Float) {
        val node = this
        // the width allocated for children
        val widthAllocatedInner = (node.width.get() ?: widthAllocated - node.margin.get().width) - node.padding.get().width
        // the getter for the width of the content based on the node's children
        val contentWidth by layout.get().computeChildrenWidth(widthAllocatedInner)
        // update the node's width
        node.computedWidthInternal = node.width.get()
                ?: if (node.fillSize) widthAllocated else larger(node.computeInternalWidth(), contentWidth + node.padding.get().width)
    }

    /**
     * Sets the height of a node based on the height of its children and the node's positioning protocols
     * @param heightAllocated the height allocated for the node, or null if no height was allocated
     */
    override fun computeHeight(heightAllocated: Float?) {
        val node = this
        // the height allocated for the children (ignoring subtracting the node's padding)
        val heightAllocatedInnerPlusPadding = (node.height.get() ?: heightAllocated ?.let { it - node.margin.get().height })
        // the getter for the height of the content based on the node's children
        val contentHeight by layout.get().computeChildrenHeight(
                node.computedWidthInternal - node.padding.get().width,
                heightAllocatedInnerPlusPadding ?.let { it - node.padding.get().height }
        )
        node.computedHeightInternal = node.height.get()
                ?: heightAllocated.takeIf { node.fillSize } ?: larger(node.computeInternalHeight(node.computedWidthInternal), contentHeight + node.padding.get().height)
    }

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

    override fun cursor(): GLFWCursor? = null

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        backgroundsInternal.reversed().forEach {
            it.draw(context, position, size)
        }

        drawChildren(children, context, position)

        foregroundsInternal.forEach {
            it.draw(context, position + padding.get().tl, size - padding.get().size)
        }
    }

    override fun getMouseHandler(position: vec2): UINode?
            =  children.reversed().firstNotNull { it.getMouseHandler(position - padding.get().tl - it.computedPosition.get()) }
            ?: super.getMouseHandler(position)

    override fun getKeyboardHandler(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): UINode?
            = children.reversed().firstNotNull { it.getKeyboardHandler(key, modifiers) } ?: this.takeIf { handlesKey(key, modifiers) } ?: super.getKeyboardHandler(key, modifiers)

    override fun getInputHandler(): UINode?
            = children.reversed().firstNotNull { it.getInputHandler() } ?: this.takeIf { handlesInput() } ?: super.getInputHandler()

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
    }
}

private fun larger(a: Float?, b: Float) = a ?.let { max(a, b) } ?: b
