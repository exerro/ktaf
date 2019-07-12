package ktaf.ui.layout

import ktaf.core.times
import ktaf.core.vec2
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.ui.node.UINode
import ktaf.ui.node.orderedChildren

abstract class UILayout {
    protected lateinit var children: List<UINode>
    /** Sets the width of children and returns the content width of those children */
    abstract fun computeChildrenWidth(widthAllocatedForChildren: Float): Lazy<Float>
    /** Sets the height of children and returns the content height of those children */
    abstract fun computeChildrenHeight(heightAllocatedForChildren: Float?): Lazy<Float>
    /** Positions the children */
    abstract fun position(width: Float, height: Float)
    /** Called prior to positioning */
    open fun begin(children: List<UINode>) { this.children = children }
    /** Called after positioning */
    open fun finish() {}

    companion object
}

fun UILayout.Companion.align(node: UINode, offset: vec2, area: vec2, alignment: vec2) {
    val (x, y) = (area - node.margin.get().size - vec2(node.computedWidthInternal, node.computedHeightInternal)) * alignment + offset
    node.computedXInternal = x
    node.computedYInternal = y
}

/** Begins positioning of a node */
fun UILayout.beginPositioning(node: UINode) {
    begin(node.orderedChildren())
    node.children.map { it.layout.get().beginPositioning(it) }
}

/** Completes positioning of a node */
fun UILayout.finishPositioning(node: UINode) {
    finish()
    node.children.map { it.layout.get().finishPositioning(it) }
}

/**
 * Sets the width of a node based on the width of its children and the node's positioning protocols
 * @param node the node to compute the width for
 * @param widthAllocated the width allocated for the node
 */
fun UILayout.computeWidthFor(node: UINode, widthAllocated: Float) {
    // the width allocated for children
    val widthAllocatedInternal = (node.width.get() ?: widthAllocated - node.margin.get().width) - node.padding.get().width
    // the getter for the width of the content based on the node's children
    val contentWidth by computeChildrenWidth(widthAllocatedInternal)
    // update the node's width
    node.computedWidthInternal = node.width.get()
            ?: if (node.fillSize) widthAllocated - node.margin.get().width else contentWidth + node.padding.get().width
}

/**
 * Sets the height of a node based on the height of its children and the node's positioning protocols
 * @param node the node to compute the height for
 * @param heightAllocated the height allocated for the node, or null if no height was allocated
 */
fun UILayout.computeHeightFor(node: UINode, heightAllocated: Float?) {
    // the height computed based off the node's width
    val computedHeight = node.computeHeight(node.computedWidthInternal)
    // the height allocated for the children (ignoring subtracting the node's padding)
    val heightAllocatedInternalPlusPadding = (computedHeight ?: heightAllocated ?.let { it - node.margin.get().height })
    // the getter for the height of the content based on the node's children
    val contentHeight by computeChildrenHeight(heightAllocatedInternalPlusPadding ?.let { it - node.padding.get().height })
    node.computedHeightInternal = computedHeight
            ?: heightAllocated.takeIf { node.fillSize } ?.let { it - node.margin.get().height } ?: contentHeight + node.padding.get().height
}

fun UILayout.computePositionForChildren(node: UINode) {
    position(
            node.computedWidthInternal - node.padding.get().width,
            node.computedHeightInternal - node.padding.get().height
    )
}
