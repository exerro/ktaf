package ktaf.gui.experimental

import geometry.vec2

interface Layout {
    fun allocateWidths(children: List<Node>, widthAvailable: Float?): List<Float?>
    fun collectWidths(widths: List<Float?>, widthAvailable: Float?): Float

    fun allocateHeights(children: List<Node>, widths: List<Float>, heightAvailable: Float?): List<Float?>
    fun collectHeights(heights: List<Float?>, heightAvailable: Float?): Float

    fun positionChildren(position: vec2, children: List<Pair<Node, vec2>>)
}

object FillLayout: Layout {
    override fun allocateWidths(children: List<Node>, widthAvailable: Float?)
            = List(children.size) { widthAvailable }

    override fun collectWidths(widths: List<Float?>, widthAvailable: Float?): Float
            = widthAvailable ?: 0f

    override fun allocateHeights(children: List<Node>, widths: List<Float>, heightAvailable: Float?)
            = List(children.size) { heightAvailable }

    override fun collectHeights(heights: List<Float?>, heightAvailable: Float?): Float
            = heightAvailable ?: 0f

    override fun positionChildren(position: vec2, children: List<Pair<Node, vec2>>) { children.forEach { (child, size) ->
        child.positioning.calculatedPosition = position
        child.positioning.calculatedSize = size
    } }
}
