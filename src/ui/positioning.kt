package ui

import kotlin.math.max

/**
 * Computes the width of all children of the node, and the width of the node itself.
 *
 * .....MMMpppOOOOOpppMMM......
 *
 * ........|_________| - width of child
 *
 * ...........|___| - allocated width for sub-children
 */
internal fun UINode.computeWidthInternal(widthAllocated: Float?) {
    // width allocated to children is either
    // * this' width minus its padding
    // * width allocated to this, minus this' margin and padding
    val widthAllocatedInternal = width ?.let { w -> w - padding.width } ?: widthAllocated ?.let { w -> w - margin.width - padding.width }
    val contentWidth by when (val l = layout) {
        // compute the width of all children
        // content width computed is the largest of children widths
        is FillLayout -> {
            children.forEach { it.computeWidthInternal(widthAllocatedInternal) }
            lazy { children .map { it.computedWidth + it.margin.width } .fold(0f, ::max) }
        }
        // compute the width of all children
        //  width allocated to child is normal width allocation, minus the spacing between elements, and finally
        //  divided by the number of horizontal blocks
        // a grid has no content width as content width scales linearly with width allocated
        is GridLayout -> {
            val wa = widthAllocatedInternal ?.let { w -> (w - (l.horizontal - 1) * l.spacing.x) / l.horizontal }
            children.forEach { it.computeWidthInternal(wa) }
            lazy { 0f }
        }
        // compute the width of all children
        //  width allocated to child is based on left|right lines OR the child's width if one or both lines are missing
        // content width computed is the rightmost of the children's right lines
        //  children without a right line have a virtual line generated at (left + width) where left defaults to 0 if
        //  there is no line
        is FreeLayout -> {
            fun eval(l: LayoutLineValue) = l.x + (widthAllocatedInternal ?: 0f) * l.y / 100f

            children.forEach { child ->
                val left = l.vLines[l.nodeLefts[child]]
                val right = l.vLines[l.nodeRights[child]]
                val width = left ?.let { right ?.let { eval(right) - eval(left) + 1 } } ?: child.width

                child.computeWidthInternal(width)
            }

            lazy { children.map { child ->
                val left = l.vLines[l.nodeLefts[child]]
                val right = l.vLines[l.nodeRights[child]]
                right ?.let { eval(right) } ?: (left ?.let { eval(left) } ?: 0f) + child.computedWidth - 1
            } .fold(0f, ::max) }
        }
        // compute the width of all children
        // content width computed is the largest of children widths
        is ListLayout -> {
            children.forEach { it.computeWidthInternal(widthAllocatedInternal) }
            lazy { children .map { it.computedWidth + it.margin.width } .fold(0f, ::max) }
        }
        // compute the width of all children
        // content width computed is the sum of children widths
        //  with no allocated width, the flow should expand horizontally as far as possible...
        is FlowLayout -> {
            children.forEach { it.computeWidthInternal(null) }
            lazy { children.map { it.computedWidth + it.margin.width } .sum() }
        }
    }

    computedWidth = width ?: widthAllocated ?.let { it - margin.width } ?: contentWidth + padding.width
}

/**
 * Computes the position and height of all children of the node, and the height of the node itself
 */
internal fun UINode.positionChildrenInternal(heightAllocated: Float?) {
    // height allocated to children is either
    // * this' height minus its padding
    // * height allocated to this, minus this' margin and padding
    val heightAllocatedInternal = height ?.let { h -> h - padding.height } ?: heightAllocated ?.let { h -> h - margin.height - padding.height }
    val flowRows = mutableListOf(mutableListOf<UINode>())
    val contentHeight by when (val l = layout) {
        is FillLayout -> {
            children.forEach { it.positionChildrenInternal(heightAllocatedInternal) }
            lazy { children .map { it.computedHeight + it.margin.height } .fold(0f, ::max) }
        }
        is GridLayout -> {
            val ha = heightAllocatedInternal ?.let { w -> (w - (l.horizontal - 1) * l.spacing.x) / l.horizontal }
            children.forEach { it.positionChildrenInternal(ha) }
            lazy { 0f }
        }
        is FreeLayout -> {
            fun evalh(l: LayoutLineValue) = l.x + (heightAllocatedInternal ?: 0f) * l.y / 100f

            children.forEach { child ->
                val top = l.hLines[l.nodeTops[child]]
                val bottom = l.hLines[l.nodeBottoms[child]]
                val height = top ?.let { bottom ?.let { evalh(bottom) - evalh(top) + 1 } } ?: child.height

                child.positionChildrenInternal(height)
            }

            lazy { children.map { child ->
                val top = l.hLines[l.nodeTops[child]]
                val bottom = l.hLines[l.nodeBottoms[child]]
                bottom ?.let { evalh(bottom) } ?: (top ?.let { evalh(top) } ?: 0f) + child.computedHeight - 1
            } .fold(0f, ::max) }
        }
        is ListLayout -> {
            children.forEach { it.positionChildrenInternal(null) }
            lazy { children .map { it.computedHeight + it.margin.height } .sum() }
        }
        is FlowLayout -> {
            var x = padding.left
            var y = padding.top
            val xOverflow = computedWidth - padding.right

            children.forEach { child ->
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

            lazy { flowRows.map { it.map { c -> c.computedHeight + c.margin.height } .fold(0f, ::max) } .sum() }
        }
    }

    computedHeight = height ?: heightAllocated ?.let { it - margin.height } ?: computeHeight(computedWidth) ?: contentHeight + padding.height

    when (val l = layout) {
        // compute height of all children
        // position children aligned within content box
        is FillLayout -> {
            children.forEach {
                it.computedX = padding.left + it.margin.left + align(l.alignment.x, computedWidth - padding.width, it.computedWidth + it.margin.width)
                it.computedY = padding.top + it.margin.top + align(l.alignment.y, computedHeight - padding.height, it.computedHeight + it.margin.height)
            }
        }
        is GridLayout -> {
            val cw = (computedWidth - padding.width - l.spacing.x * (l.horizontal - 1)) / l.horizontal
            val ch = (computedHeight - padding.height - l.spacing.y * (l.vertical - 1)) / l.vertical

            children
                    .mapIndexed { i, child ->
                        Triple(child, i % l.horizontal, i / l.horizontal)
                    }
                    .forEach { (it, x, y) ->
                        it.computedX = padding.left + it.margin.left + (cw + l.spacing.x) * x + align(l.alignment.x, cw, it.computedWidth + it.margin.width)
                        it.computedY = padding.top + it.margin.top + (ch + l.spacing.y) * y + align(l.alignment.y, ch, it.computedHeight + it.margin.height)
                    }
        }
        is FreeLayout -> {
            fun evalw(l: LayoutLineValue?) = l ?.let { l.x + computedWidth * l.y / 100f }
            fun evalh(l: LayoutLineValue?) = l ?.let { l.x + computedHeight * l.y / 100f }

            children.forEach { child ->
                val top = evalh(l.hLines[l.nodeTops[child]]) ?: 0f
                val left = evalw(l.vLines[l.nodeLefts[child]]) ?: 0f
                val bottom = evalh(l.hLines[l.nodeBottoms[child]]) ?: top + child.computedHeight
                val right = evalw(l.vLines[l.nodeRights[child]]) ?: left + child.computedWidth

                child.computedX = padding.left + child.margin.left + left + align(l.alignment.x, right - left, child.computedWidth + child.margin.width)
                child.computedY = padding.top + child.margin.top + top + align(l.alignment.y, bottom - top, child.computedHeight + child.margin.height)
            }
        }
        is ListLayout -> {
            var y = padding.top + l.spacing.init(children.size, computedHeight, contentHeight)
            val yd = l.spacing.iter(children.size, computedHeight, contentHeight)

            children.forEach {
                it.computedX = padding.left + it.margin.left + align(l.alignment, computedWidth - padding.width, it.computedWidth + it.margin.width)
                it.computedY = y + it.margin.top
                y += it.computedHeight + it.margin.height + yd
            }
        }
        is FlowLayout -> {
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
    }
}

fun align(alignment: Float, size: Float, isize: Float)
        = alignment * (size - isize)
