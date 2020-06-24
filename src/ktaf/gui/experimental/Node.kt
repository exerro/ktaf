package ktaf.gui.experimental

import geometry.vec2

class Node: Element() {
    override val node = this

    val positioning = Positioning()
    val events = Events()
    val children: List<Node>
    var layout: Layout = FillLayout

    ////////////////////////////////////////////////////////////////////////////

    internal fun calculateWidth(widthAvailable: Float?): Float {
        val internalWidth = (positioning.width ?: widthAvailable) ?.let { it - positioning.padding.width }
        val allocations = layout.allocateWidths(children, internalWidth)

        widths = children.mapIndexed { i, child -> child.calculateWidth(allocations[i]) }

        return positioning.width
                ?: widthAvailable.takeIf { positioning.expand }
                ?: layout.collectWidths(widths, widthAvailable)
    }

    internal fun calculateHeight(width: Float, heightAvailable: Float?): Float {
        val internalHeight = (positioning.height ?: heightAvailable) ?.let { it - positioning.padding.width }
        val allocations = layout.allocateHeights(children, internalHeight)

        heights = children.mapIndexed { i, child -> child.calculateHeight(widths[i], allocations[i]) }

        return positioning.height
                ?: heightAvailable.takeIf { positioning.expand }
                ?: layout.collectHeights(heights, heightAvailable)
    }

    internal fun calculatePosition() {
        val sizes = widths.zip(heights).map { (w, h) -> vec2(w, h) }
        layout.positionChildren(positioning.calculatedPosition, children.zip(sizes))
        children.forEach { it.calculatePosition() }
    }

    ////////////////////////////////////////////////////////////////////////////

    fun addChild(child: Node) {
        internalChildren.add(child)
    }

    fun removeChild(child: Node) {
        internalChildren.remove(child)
    }

    fun clearChildren() {
        internalChildren.clear()
    }

    ////////////////////////////////////////////////////////////////////////////

    private lateinit var widths: List<Float>
    private lateinit var heights: List<Float>
    private val internalChildren = mutableListOf<Node>()

    init {
        children = internalChildren
    }
}
