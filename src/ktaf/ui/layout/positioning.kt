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

    when (val l = layout.get()) {
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
    children.forEach {
        it.computedXCachedSetter = it.margin.get().left + align(l.alignment.get().x, computedWidthCachedSetter - padding.get().width, it.computedWidthCachedSetter + it.margin.get().width)
        it.computedYCachedSetter = it.margin.get().top + align(l.alignment.get().y, computedHeightCachedSetter - padding.get().height, it.computedHeightCachedSetter + it.margin.get().height)
    }
}

// TODO: description
private fun UINode.positionChildrenGrid(l: GridLayout) {
    val cw = (computedWidthCachedSetter - padding.get().width - l.spacing.get().x * (l.columns.get() - 1)) / l.columns.get()
    val ch = (computedHeightCachedSetter - padding.get().height - l.spacing.get().y * (l.rows.get() - 1)) / l.rows.get()

    children
            .mapIndexed { i, child ->
                Triple(child, i % l.columns.get(), i / l.columns.get())
            }
            .forEach { (it, x, y) ->
                it.computedXCachedSetter = it.margin.get().left + (cw + l.spacing.get().x) * x + align(l.alignment.get().x, cw, it.computedWidthCachedSetter + it.margin.get().width)
                it.computedYCachedSetter = it.margin.get().top + (ch + l.spacing.get().y) * y + align(l.alignment.get().y, ch, it.computedHeightCachedSetter + it.margin.get().height)
            }
}

// TODO: description
private fun UINode.positionChildrenFree(l: FreeLayout) {
    fun evalw(l: LayoutLineValue?) = l ?.let { l.fixed + (computedWidthCachedSetter - padding.get().width) * l.ratio }
    fun evalh(l: LayoutLineValue?) = l ?.let { l.fixed + (computedHeightCachedSetter - padding.get().height) * l.ratio }

    children.forEach { child ->
        val top = evalh(l.hLines[l.nodeTops[child]]) ?: 0f
        val left = evalw(l.vLines[l.nodeLefts[child]]) ?: 0f
        val bottom = evalh(l.hLines[l.nodeBottoms[child]]) ?: top + child.computedHeightCachedSetter
        val right = evalw(l.vLines[l.nodeRights[child]]) ?: left + child.computedWidthCachedSetter

        child.computedXCachedSetter = child.margin.get().left + left + align(l.alignment.get().x, right - left, child.computedWidthCachedSetter + child.margin.get().width)
        child.computedYCachedSetter = child.margin.get().top + top + align(l.alignment.get().y, bottom - top, child.computedHeightCachedSetter + child.margin.get().height)
    }
}

// TODO: description
private fun UINode.positionChildrenList(l: ListLayout) {
    val contentHeight = children.map { it.computedHeightCachedSetter } .sum()
    var y = l.spacing.get().init(children.size, computedHeightCachedSetter, contentHeight)
    val yd = l.spacing.get().iter(children.size, computedHeightCachedSetter, contentHeight)

    children.forEach {
        it.computedXCachedSetter = it.margin.get().left + align(l.alignment.get(), computedWidthCachedSetter - padding.get().width, it.computedWidthCachedSetter + it.margin.get().width)
        it.computedYCachedSetter = y + it.margin.get().top
        y += it.computedHeightCachedSetter + it.margin.get().height + yd
    }
}

// TODO: description
private fun UINode.positionChildrenFlow(l: FlowLayout, flowRows: List<List<UINode>>) {
    val contentHeight = flowRows.map { it.map { node -> node.computedHeightCachedSetter } .fold(0f, ::max) } .sum()
    var yOffset = l.verticalSpacing.get().init(flowRows.size, computedHeightCachedSetter, contentHeight)
    val yd = l.verticalSpacing.get().iter(flowRows.size, computedHeightCachedSetter, contentHeight)

    flowRows.forEach { row ->
        val rowWidth = row.map { c -> c.computedWidthCachedSetter + c.margin.get().width } .sum()
        var xOffset = l.horizontalSpacing.get().init(row.size, computedWidthCachedSetter, rowWidth)
        val xd = l.horizontalSpacing.get().iter(row.size, computedWidthCachedSetter, rowWidth)

        row.forEach { c ->
            c.computedXCachedSetter = xOffset + c.margin.get().left
            c.computedYCachedSetter = yOffset + c.margin.get().top

            xOffset += xd + c.margin.get().width + c.computedWidthCachedSetter
        }

        yOffset += yd + row.map { it.margin.get().height + it.computedHeightCachedSetter }.fold(0f, ::max)
    }
}

fun align(alignment: Float, size: Float, isize: Float)
        = alignment * (size - isize)
