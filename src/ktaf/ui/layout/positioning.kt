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
        it.computedX = it.margin.left + align(l.alignment.x, computedWidth - padding.width, it.computedWidth + it.margin.width)
        it.computedY = it.margin.top + align(l.alignment.y, computedHeight - padding.height, it.computedHeight + it.margin.height)
    }
}

// TODO: description
private fun UINode.positionChildrenGrid(l: GridLayout) {
    val cw = (computedWidth - padding.width - l.spacing.x * (l.columns - 1)) / l.columns
    val ch = (computedHeight - padding.height - l.spacing.y * (l.rows - 1)) / l.rows

    childrenInternal
            .mapIndexed { i, child ->
                Triple(child, i % l.columns, i / l.columns)
            }
            .forEach { (it, x, y) ->
                it.computedX = it.margin.left + (cw + l.spacing.x) * x + align(l.alignment.x, cw, it.computedWidth + it.margin.width)
                it.computedY = it.margin.top + (ch + l.spacing.y) * y + align(l.alignment.y, ch, it.computedHeight + it.margin.height)
            }
}

// TODO: description
private fun UINode.positionChildrenFree(l: FreeLayout) {
    fun evalw(l: LayoutLineValue?) = l ?.let { l.fixed + (computedWidth - padding.width) * l.ratio }
    fun evalh(l: LayoutLineValue?) = l ?.let { l.fixed + (computedHeight - padding.height) * l.ratio }

    childrenInternal.forEach { child ->
        val top = evalh(l.hLines[l.nodeTops[child]]) ?: 0f
        val left = evalw(l.vLines[l.nodeLefts[child]]) ?: 0f
        val bottom = evalh(l.hLines[l.nodeBottoms[child]]) ?: top + child.computedHeight
        val right = evalw(l.vLines[l.nodeRights[child]]) ?: left + child.computedWidth

        child.computedX = child.margin.left + left + align(l.alignment.x, right - left, child.computedWidth + child.margin.width)
        child.computedY = child.margin.top + top + align(l.alignment.y, bottom - top, child.computedHeight + child.margin.height)
    }
}

// TODO: description
private fun UINode.positionChildrenList(l: ListLayout) {
    val contentHeight = childrenInternal.map { it.computedHeight } .sum()
    var y = l.spacing.init(childrenInternal.size, computedHeight, contentHeight)
    val yd = l.spacing.iter(childrenInternal.size, computedHeight, contentHeight)

    childrenInternal.forEach {
        it.computedX = it.margin.left + align(l.alignment, computedWidth - padding.width, it.computedWidth + it.margin.width)
        it.computedY = y + it.margin.top
        y += it.computedHeight + it.margin.height + yd
    }
}

// TODO: description
private fun UINode.positionChildrenFlow(l: FlowLayout, flowRows: List<List<UINode>>) {
    val contentHeight = flowRows.map { it.map { node -> node.computedHeight } .fold(0f, ::max) } .sum()
    var yOffset = l.verticalSpacing.init(flowRows.size, computedHeight, contentHeight)
    val yd = l.verticalSpacing.iter(flowRows.size, computedHeight, contentHeight)

    flowRows.forEach { row ->
        val rowWidth = row.map { c -> c.computedWidth + c.margin.width } .sum()
        var xOffset = l.horizontalSpacing.init(row.size, computedWidth, rowWidth)
        val xd = l.horizontalSpacing.iter(row.size, computedWidth, rowWidth)

        row.forEach { c ->
            c.computedX += xOffset + c.margin.left
            c.computedY += yOffset + c.margin.left

            xOffset += xd
        }

        yOffset += yd
    }
}

fun align(alignment: Float, size: Float, isize: Float)
        = alignment * (size - isize)
