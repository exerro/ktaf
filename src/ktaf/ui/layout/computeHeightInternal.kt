package ktaf.ui.layout

import ktaf.ui.UINode
import ktaf.ui.height
import ktaf.ui.width
import kotlin.math.max

/**
 * Computes the position and height of all children of the node, and the height of the node itself
 */
internal fun UINode.computeHeightInternal(heightAllocated: Float?): MutableList<MutableList<UINode>> {
    // height allocated to children is either
    // * this' height minus its padding
    // * height allocated to this, minus this' margin and padding
    val heightAllocatedInternal = computeHeight(computedWidth) ?.let { h -> h - padding.height } ?: heightAllocated ?.let { h -> h - margin.height - padding.height }
    val flowRows = mutableListOf(mutableListOf<UINode>())
    val contentHeight by when (val l = layout) {
        is FillLayout -> computeFillHeight(heightAllocatedInternal)
        is GridLayout -> computeGridHeight(l, heightAllocatedInternal)
        is FreeLayout -> computeFreeHeight(l, heightAllocatedInternal)
        is ListLayout -> computeListHeight(l, heightAllocatedInternal)
        is FlowLayout -> computeFlowHeight(l, heightAllocatedInternal, flowRows)
    }

    computedHeight = height ?: heightAllocated ?.let { h -> h - margin.height } .takeIf { fillAllocatedSize } ?: contentHeight + padding.height

    return flowRows
}

// TODO: description
private fun UINode.computeFillHeight(heightAllocatedInternal: Float?): Lazy<Float> {
    childrenInternal.forEach { it.positionChildrenInternal(heightAllocatedInternal) }
    return lazy { childrenInternal .map { it.computedHeight + it.margin.height } .fold(0f, ::max) }
}

// TODO: description
private fun UINode.computeGridHeight(l: GridLayout, heightAllocatedInternal: Float?): Lazy<Float> {
    val ha = heightAllocatedInternal ?.let { h -> (h - (l.rows - 1) * l.spacing.y) / l.rows }
    childrenInternal.forEach { it.positionChildrenInternal(ha) }
    return lazy {
        val rows = childrenInternal.chunked(l.columns)
        rows.map { it.map { node -> node.computedHeight } .fold(0f, ::max) } .sum() + l.spacing.y * (l.rows - 1)
    }
}

// TODO: description
private fun UINode.computeFreeHeight(l: FreeLayout, heightAllocatedInternal: Float?): Lazy<Float> {
    fun evalh(l: LayoutLineValue) = l.fixed + (heightAllocatedInternal ?: 0f) * l.ratio

    childrenInternal.forEach { child ->
        val top = l.hLines[l.nodeTops[child]]
        val bottom = l.hLines[l.nodeBottoms[child]]
        val height = top ?.let { bottom ?.let { evalh(bottom) - evalh(top) + 1 } } ?: child.height

        child.positionChildrenInternal(height)
    }

    return lazy { childrenInternal.map { child ->
        val top = l.hLines[l.nodeTops[child]]
        val bottom = l.hLines[l.nodeBottoms[child]]
        bottom ?.let { evalh(bottom) } ?: (top ?.let { evalh(top) } ?: 0f) + child.computedHeight - 1
    } .fold(0f, ::max) }
}

// TODO: description
private fun UINode.computeListHeight(l: ListLayout, heightAllocatedInternal: Float?): Lazy<Float> {
    childrenInternal.forEach { it.positionChildrenInternal(null) }
    return lazy { childrenInternal .map { it.computedHeight + it.margin.height } .sum() }
}

// TODO: description
private fun UINode.computeFlowHeight(l: FlowLayout, heightAllocatedInternal: Float?, flowRows: MutableList<MutableList<UINode>>): Lazy<Float> {
    var x = 0f
    var y = 0f
    val xOverflow = computedWidth - padding.right

    childrenInternal.forEach { child ->
        child.positionChildrenInternal(null)

        if (x + child.margin.width + child.computedWidth > xOverflow) {
            x = padding.left
            y += flowRows.last().map { it.margin.height + it.computedHeight } .fold(0f, ::max)
            flowRows.add(mutableListOf())
        }

        child.computedX = x
        child.computedY = y

        flowRows.last().add(child)
        x += child.margin.width + child.computedWidth
    }

    return lazy { flowRows.map { it.map { c -> c.computedHeight + c.margin.height } .fold(0f, ::max) } .sum() }
}
