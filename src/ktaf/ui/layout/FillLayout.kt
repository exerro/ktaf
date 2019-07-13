package ktaf.ui.layout

import ktaf.core.KTAFValue
import ktaf.core.vec2

class FillLayout: UILayout() {
    val alignment = KTAFValue(vec2(0f))

    override fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float> {
        // compute the width for each child where allocated width fills the area
        UILayout.fillChildrenWidths(children, widthAllocatedForContent)
        // return the largest of the children's widths as the content width
        return lazy { UILayout.maximumChildWidth(children) }
    }

    override fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float> {
        // compute the height for each child where allocated height fills the area
        UILayout.fillChildrenHeights(children, heightAllocatedForContent)
        // return the largest of the children's heights as the content height
        return lazy { UILayout.maximumChildHeight(children) }
    }

    override fun position(width: Float, height: Float) {
        UILayout.positionChildrenChildren(children)
        // position each child in the area with an alignment
        children.forEach { align(it, vec2(0f), vec2(width, height), alignment.get()) }
    }
}
