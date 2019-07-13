package ktaf.ui.layout

import ktaf.core.KTAFValue
import ktaf.core.vec2

class FillLayout: UILayout() {
    val alignment = KTAFValue(vec2(0f))

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
        // position each child in the area with an alignment
        children.forEach { align(it, vec2(0f), vec2(width, height), alignment.get()) }
    }
}
