package ktaf.ui.layout

//// TODO: description
//private fun UINode.computeGridHeight(l: GridLayout, heightAllocatedInternal: Float?): Lazy<Float> {
//    val ha = heightAllocatedInternal ?.let { h -> (h - (l.rows.get() - 1) * l.spacing.get().y) / l.rows.get() }
//    children.forEach { it.positionChildrenInternal(ha) }
//    return lazy {
//        val rows = children.chunked(l.columns.get())
//        rows.map { it.map { node -> node.computedHeightInternal } .fold(0f, ::max) } .sum() + l.spacing.get().y * (l.rows.get() - 1)
//    }
//}
//
//// TODO: description
//private fun UINode.computeFreeHeight(l: FreeLayout, heightAllocatedInternal: Float?): Lazy<Float> {
//    fun evalh(l: LayoutLineValue) = l.fixed + (heightAllocatedInternal ?: 0f) * l.ratio
//
//    children.forEach { child ->
//        val top = l.hLines[l.nodeTops[child]]
//        val bottom = l.hLines[l.nodeBottoms[child]]
//        val height = top ?.let { bottom ?.let { evalh(bottom) - evalh(top) + 1 } } ?: child.height.get() ?: heightAllocatedInternal
//
//        child.positionChildrenInternal(height)
//    }
//
//    return lazy { children.map { child ->
//        val top = l.hLines[l.nodeTops[child]]
//        val bottom = l.hLines[l.nodeBottoms[child]]
//        bottom ?.let { evalh(bottom) } ?: (top ?.let { evalh(top) } ?: 0f) + child.computedHeightInternal - 1
//    } .fold(0f, ::max) }
//}
//
//// TODO: description
//private fun UINode.computeListHeight(l: ListLayout): Lazy<Float> {
//    children.forEach { it.positionChildrenInternal(null) }
//    return lazy { children .map { it.computedHeightInternal + it.margin.get().height } .sum() + (children.size - 1) * l.spacing.get().fixed() }
//}
//
//// TODO: description
//private fun UINode.computeFlowHeight(flowRows: MutableList<MutableList<UINode>>): Lazy<Float> {
//    var x = 0f
//    val xOverflow = computedWidthInternal - padding.get().right
//
//    children.forEach { child ->
//        child.positionChildrenInternal(null)
//
//        if (x + child.margin.get().width + child.computedWidthInternal > xOverflow) {
//            x = padding.get().left
//            flowRows.add(mutableListOf())
//        }
//
//        flowRows.last().add(child)
//        x += child.margin.get().width + child.computedWidthInternal
//    }
//
//    return lazy { flowRows.map { it.map { c -> c.computedHeightInternal + c.margin.get().height } .fold(0f, ::max) } .sum() }
//}
