package ktaf.ui.layout

import geometry.vec2
import ktaf.core.KTAFValue
import ktaf.ui.node.UINode

class ViewLayout: UILayout() {
    val alignment = KTAFValue(vec2(0f))
    val locationX = KTAFValue(0)
    val locationY = KTAFValue(0)

    fun location(node: UINode, x: Int, y: Int) {
        locations[node] = Pair(x, y)
    }

    fun location(node: UINode) = locations[node]

    fun location(x: Int, y: Int) {
        locationX(x)
        locationY(y)
    }

    // compute the width for each child where allocated width fills the area
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.fillChildrenWidths(children, widthAllocatedForContent)
    }

    // compute the height for each child where allocated height fills the area
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        UILayout.fillChildrenHeights(children, heightAllocatedForContent)
    }

    // return the largest of the children's widths as the content width
    override fun computeChildrenWidth() = UILayout.maximumChildWidth(children)

    // return the largest of the children's heights as the content height
    override fun computeChildrenHeight() = UILayout.maximumChildHeight(children)

    override fun position(width: Float, height: Float) {
        UILayout.positionChildrenChildren(children)
        // position each child in the area with an alignment and offset based on the current location
        children.forEach {
            val dx = (locations[it] ?.let { (x, _) -> x } ?: 0) - locationX.get()
            val dy = (locations[it] ?.let { (_, y) -> y } ?: 0) - locationY.get()
            align(it, vec2(width * dx, height * dy), vec2(width, height), alignment.get())
        }
    }

    private val locations = mutableMapOf<UINode, Pair<Int, Int>>()
}
