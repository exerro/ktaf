package ktaf.ui.layout

// width allocated to child is normal width allocation, minus the spacing between elements, and finally
// divided by the number of horizontal blocks
// content width is sum of the largest widths of each column plus padding
//private fun UINode.computeGridWidth(l: GridLayout, widthAllocatedInternal: Float): Lazy<Float> {
//    val wa = (widthAllocatedInternal - (l.columns.get() - 1) * l.spacing.get().x) / l.columns.get()
//    children.forEach { it.computeWidthInternal(wa) }
//    return lazy {
//        val rows = children.chunked(l.columns.get())
//        val columns = (0 until l.columns.get()).map { column -> rows.map { it.getOrNull(column)?.computedWidthInternal ?: 0f } }
//        columns.map { it.fold(0f, ::max) } .sum() + l.spacing.get().x * (l.columns.get() - 1)
//    }
//}
//
//// width allocated to child is based on left|right lines OR the child's width if one or both lines are missing
//// content width computed is the rightmost of the children's right lines
////  children without a right line have a virtual line generated at (left + width) where left defaults to 0 if
////  there is no line
//private fun UINode.computeFreeWidth(l: FreeLayout, widthAllocatedInternal: Float): Lazy<Float> {
//    fun eval(l: LayoutLineValue) = l.fixed + widthAllocatedInternal * l.ratio
//
//    children.forEach { child ->
//        val left = l.vLines[l.nodeLefts[child]]
//        val right = l.vLines[l.nodeRights[child]]
//        val width = left ?.let { right ?.let { eval(right) - eval(left) + 1 } } ?: child.width.get() ?: widthAllocatedInternal
//
//        child.computeWidthInternal(width)
//    }
//
//    return lazy { children.map { child ->
//        val left = l.vLines[l.nodeLefts[child]]
//        val right = l.vLines[l.nodeRights[child]]
//        right ?.let { eval(right) } ?: (left ?.let { eval(left) } ?: 0f) + child.computedWidthInternal - 1
//    } .fold(0f, ::max) }
//}
//
//// width allocated to each child is width allocated to this
//// content width computed is the largest of children widths
//private fun UINode.computeListWidth(widthAllocatedInternal: Float): Lazy<Float> {
//    children.forEach { it.computeWidthInternal(widthAllocatedInternal) }
//    return lazy { children .map { it.computedWidthInternal + it.margin.get().width } .fold(0f, ::max) }
//}
//
//// width allocated to each child is width allocated to this
//// content width computed is the sum of children widths
//private fun UINode.computeFlowWidth(widthAllocatedInternal: Float): Lazy<Float> {
//    children.forEach { it.computeWidthInternal(widthAllocatedInternal) }
//    return lazy { children.map { it.computedWidthInternal + it.margin.get().width } .sum() }
//}
