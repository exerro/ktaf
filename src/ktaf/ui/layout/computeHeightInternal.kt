package ktaf.ui.layout

import ktaf.ui.node.UINode
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
    val computedHeight = computeHeight(computedWidthCachedSetter)
    val heightAllocatedInternal by lazy { computedHeight ?.let { h -> h - padding.get().height } ?: heightAllocated ?.let { h -> h - margin.get().height - padding.get().height } }
    val flowRows = mutableListOf(mutableListOf<UINode>())
    val contentHeight by when (val l = layout.get()) {
        is FillLayout -> computeFillHeight(heightAllocatedInternal)
        is GridLayout -> computeGridHeight(l, heightAllocatedInternal)
        is FreeLayout -> computeFreeHeight(l, heightAllocatedInternal)
        is ListLayout -> computeListHeight()
        is FlowLayout -> computeFlowHeight(flowRows)
    }

    computedHeightCachedSetter = computedHeight ?: heightAllocated ?.let { h -> h - margin.get().height } .takeIf { fillAllocatedSize } ?: contentHeight + padding.get().height

    return flowRows
}

// TODO: description
private fun UINode.computeFillHeight(heightAllocatedInternal: Float?): Lazy<Float> {
    children.forEach { it.positionChildrenInternal(heightAllocatedInternal) }
    return lazy { children .map { it.computedHeightCachedSetter + it.margin.get().height } .fold(0f, ::max) }
}

// TODO: description
private fun UINode.computeGridHeight(l: GridLayout, heightAllocatedInternal: Float?): Lazy<Float> {
    val ha = heightAllocatedInternal ?.let { h -> (h - (l.rows.get() - 1) * l.spacing.get().y) / l.rows.get() }
    children.forEach { it.positionChildrenInternal(ha) }
    return lazy {
        val rows = children.chunked(l.columns.get())
        rows.map { it.map { node -> node.computedHeightCachedSetter } .fold(0f, ::max) } .sum() + l.spacing.get().y * (l.rows.get() - 1)
    }
}

// TODO: description
private fun UINode.computeFreeHeight(l: FreeLayout, heightAllocatedInternal: Float?): Lazy<Float> {
    fun evalh(l: LayoutLineValue) = l.fixed + (heightAllocatedInternal ?: 0f) * l.ratio

    children.forEach { child ->
        val top = l.hLines[l.nodeTops[child]]
        val bottom = l.hLines[l.nodeBottoms[child]]
        val height = top ?.let { bottom ?.let { evalh(bottom) - evalh(top) + 1 } } ?: child.height.get() ?: heightAllocatedInternal

        child.positionChildrenInternal(height)
    }

    return lazy { children.map { child ->
        val top = l.hLines[l.nodeTops[child]]
        val bottom = l.hLines[l.nodeBottoms[child]]
        bottom ?.let { evalh(bottom) } ?: (top ?.let { evalh(top) } ?: 0f) + child.computedHeightCachedSetter - 1
    } .fold(0f, ::max) }
}

// TODO: description
private fun UINode.computeListHeight(): Lazy<Float> {
    children.forEach { it.positionChildrenInternal(null) }
    return lazy { children .map { it.computedHeightCachedSetter + it.margin.get().height } .sum() }
}

// TODO: description
private fun UINode.computeFlowHeight(flowRows: MutableList<MutableList<UINode>>): Lazy<Float> {
    var x = 0f
    val xOverflow = computedWidthCachedSetter - padding.get().right

    children.forEach { child ->
        child.positionChildrenInternal(null)

        if (x + child.margin.get().width + child.computedWidthCachedSetter > xOverflow) {
            x = padding.get().left
            flowRows.add(mutableListOf())
        }

        flowRows.last().add(child)
        x += child.margin.get().width + child.computedWidthCachedSetter
    }

    return lazy { flowRows.map { it.map { c -> c.computedHeightCachedSetter + c.margin.get().height } .fold(0f, ::max) } .sum() }
}
