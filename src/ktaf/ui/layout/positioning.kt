package ktaf.ui.layout

import ktaf.ui.UINode
import ktaf.ui.height
import ktaf.ui.width
import kotlin.math.max

/**
 * Computes the position and height of all children of the node, and the height of the node itself
 */
internal fun UINode.positionChildrenInternal(heightAllocated: Float?) {
    val flowRows = computeHeightInternal(heightAllocated)

    when (val l = layout) {
        is FillLayout -> positionChildrenFill(l)
        is GridLayout -> positionChildrenGrid(l)
        is FreeLayout -> positionChildrenFree(l)
        is ListLayout -> positionChildrenList(l)
        is FlowLayout -> positionChildrenFlow(l, flowRows)
    }
}

// position children aligned within content box
// TODO: description
private fun UINode.positionChildrenFill(l: FillLayout) {
    childrenInternal.forEach {
        it.computedXInternal = it.margin.left + align(l.alignment.x, computedWidthInternal - padding.width, it.computedWidthInternal + it.margin.width)
        it.computedYInternal = it.margin.top + align(l.alignment.y, computedHeightInternal - padding.height, it.computedHeightInternal + it.margin.height)
    }
}

// TODO: description
private fun UINode.positionChildrenGrid(l: GridLayout) {
    val cw = (computedWidthInternal - padding.width - l.spacing.x * (l.columns - 1)) / l.columns
    val ch = (computedHeightInternal - padding.height - l.spacing.y * (l.rows - 1)) / l.rows

    childrenInternal
            .mapIndexed { i, child ->
                Triple(child, i % l.columns, i / l.columns)
            }
            .forEach { (it, x, y) ->
                it.computedXInternal = it.margin.left + (cw + l.spacing.x) * x + align(l.alignment.x, cw, it.computedWidthInternal + it.margin.width)
                it.computedYInternal = it.margin.top + (ch + l.spacing.y) * y + align(l.alignment.y, ch, it.computedHeightInternal + it.margin.height)
            }
}

// TODO: description
private fun UINode.positionChildrenFree(l: FreeLayout) {
    fun evalw(l: LayoutLineValue?) = l ?.let { l.fixed + (computedWidthInternal - padding.width) * l.ratio }
    fun evalh(l: LayoutLineValue?) = l ?.let { l.fixed + (computedHeightInternal - padding.height) * l.ratio }

    childrenInternal.forEach { child ->
        val top = evalh(l.hLines[l.nodeTops[child]]) ?: 0f
        val left = evalw(l.vLines[l.nodeLefts[child]]) ?: 0f
        val bottom = evalh(l.hLines[l.nodeBottoms[child]]) ?: top + child.computedHeightInternal
        val right = evalw(l.vLines[l.nodeRights[child]]) ?: left + child.computedWidthInternal

        child.computedXInternal = child.margin.left + left + align(l.alignment.x, right - left, child.computedWidthInternal + child.margin.width)
        child.computedYInternal = child.margin.top + top + align(l.alignment.y, bottom - top, child.computedHeightInternal + child.margin.height)
    }
}

// TODO: description
private fun UINode.positionChildrenList(l: ListLayout) {
    val contentHeight = childrenInternal.map { it.computedHeightInternal } .sum()
    var y = l.spacing.init(childrenInternal.size, computedHeightInternal, contentHeight)
    val yd = l.spacing.iter(childrenInternal.size, computedHeightInternal, contentHeight)

    childrenInternal.forEach {
        it.computedXInternal = it.margin.left + align(l.alignment, computedWidthInternal - padding.width, it.computedWidthInternal + it.margin.width)
        it.computedYInternal = y + it.margin.top
        y += it.computedHeightInternal + it.margin.height + yd
    }
}

// TODO: description
private fun UINode.positionChildrenFlow(l: FlowLayout, flowRows: List<List<UINode>>) {
    val contentHeight = flowRows.map { it.map { node -> node.computedHeightInternal } .fold(0f, ::max) } .sum()
    var yOffset = l.verticalSpacing.init(flowRows.size, computedHeightInternal, contentHeight)
    val yd = l.verticalSpacing.iter(flowRows.size, computedHeightInternal, contentHeight)

    flowRows.forEach { row ->
        val rowWidth = row.map { c -> c.computedWidthInternal + c.margin.width } .sum()
        var xOffset = l.horizontalSpacing.init(row.size, computedWidthInternal, rowWidth)
        val xd = l.horizontalSpacing.iter(row.size, computedWidthInternal, rowWidth)

        row.forEach { c ->
            c.computedXInternal = xOffset + c.margin.left
            c.computedYInternal = yOffset + c.margin.left

            xOffset += xd + c.margin.width + c.computedWidthInternal
        }

        yOffset += yd + row.map { it.margin.height + it.computedHeightInternal }.fold(0f, ::max)
    }
}

fun align(alignment: Float, size: Float, isize: Float)
        = alignment * (size - isize)
