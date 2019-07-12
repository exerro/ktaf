package ktaf.ui.layout

//// TODO: description
//private fun UINode.positionChildrenGrid(l: GridLayout) {
//    val cw = (computedWidthInternal - padding.get().width - l.spacing.get().x * (l.columns.get() - 1)) / l.columns.get()
//    val ch = (computedHeightInternal - padding.get().height - l.spacing.get().y * (l.rows.get() - 1)) / l.rows.get()
//
//    children
//            .mapIndexed { i, child ->
//                Triple(child, i % l.columns.get(), i / l.columns.get())
//            }
//            .forEach { (it, x, y) ->
//                it.computedXInternal = it.margin.get().left + (cw + l.spacing.get().x) * x + align(l.alignment.get().x, cw, it.computedWidthInternal + it.margin.get().width)
//                it.computedYInternal = it.margin.get().top + (ch + l.spacing.get().y) * y + align(l.alignment.get().y, ch, it.computedHeightInternal + it.margin.get().height)
//            }
//}
//
//// TODO: description
//private fun UINode.positionChildrenFree(l: FreeLayout) {
//    fun evalw(l: LayoutLineValue?) = l ?.let { l.fixed + (computedWidthInternal - padding.get().width) * l.ratio }
//    fun evalh(l: LayoutLineValue?) = l ?.let { l.fixed + (computedHeightInternal - padding.get().height) * l.ratio }
//
//    children.forEach { child ->
//        val top = evalh(l.hLines[l.nodeTops[child]]) ?: 0f
//        val left = evalw(l.vLines[l.nodeLefts[child]]) ?: 0f
//        val bottom = evalh(l.hLines[l.nodeBottoms[child]]) ?: top + child.computedHeightInternal
//        val right = evalw(l.vLines[l.nodeRights[child]]) ?: left + child.computedWidthInternal
//
//        child.computedXInternal = child.margin.get().left + left + align(l.alignment.get().x, right - left, child.computedWidthInternal + child.margin.get().width)
//        child.computedYInternal = child.margin.get().top + top + align(l.alignment.get().y, bottom - top, child.computedHeightInternal + child.margin.get().height)
//    }
//}
//
//// TODO: description
//private fun UINode.positionChildrenList(l: ListLayout) {
//    val contentHeight = children.map { it.computedHeightInternal } .sum()
//    val (offset, spacing) = l.spacing.get().evaluate(computedHeightInternal - padding.get().height - contentHeight, children.size)
//    var y = offset
//
//    children.forEach {
//        it.computedXInternal = it.margin.get().left + align(l.alignment.get(), computedWidthInternal - padding.get().width, it.computedWidthInternal + it.margin.get().width)
//        it.computedYInternal = y + it.margin.get().top
//        y += it.computedHeightInternal + it.margin.get().height + spacing
//    }
//}
//
//// TODO: description
//private fun UINode.positionChildrenFlow(l: FlowLayout, flowRows: List<List<UINode>>) {
//    val contentHeight = flowRows.map { it.map { node -> node.computedHeightInternal } .fold(0f, ::max) } .sum()
//    var yOffset = l.verticalSpacing.get().init(flowRows.size, computedHeightInternal, contentHeight)
//    val yd = l.verticalSpacing.get().iter(flowRows.size, computedHeightInternal, contentHeight)
//
//    flowRows.forEach { row ->
//        val rowWidth = row.map { c -> c.computedWidthInternal + c.margin.get().width } .sum()
//        var xOffset = l.horizontalSpacing.get().init(row.size, computedWidthInternal, rowWidth)
//        val xd = l.horizontalSpacing.get().iter(row.size, computedWidthInternal, rowWidth)
//
//        row.forEach { c ->
//            c.computedXInternal = xOffset + c.margin.get().left
//            c.computedYInternal = yOffset + c.margin.get().top
//
//            xOffset += xd + c.margin.get().width + c.computedWidthInternal
//        }
//
//        yOffset += yd + row.map { it.margin.get().height + it.computedHeightInternal }.fold(0f, ::max)
//    }
//}
//
//fun align(alignment: Float, size: Float, isize: Float)
//        = alignment * (size - isize)
