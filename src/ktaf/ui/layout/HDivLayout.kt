package ktaf.ui.layout

import ktaf.core.KTAFValue
import ktaf.core.vec2

class HDivLayout: UILayout() {
    val alignment = KTAFValue(vec2(0.5f))
    val spacing = KTAFValue(0f) // TODO: use proper spacing

    // compute the width for each child where allocated width fills the area divided evenly amongst children
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.setChildrenWidths(children, widthAllocatedForContent ?.let { w -> (w - (children.size - 1) * spacing.get()) / children.size })
    }

    // compute the height for each child where allocated height fills the area
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        UILayout.setChildrenHeights(children, heightAllocatedForContent)
    }

    // return the sum of the widths for each child plus appropriate spacing
    override fun computeChildrenWidth() = UILayout.sumChildrenWidth(children) + spacing.get() * (children.size - 1)

    // return the sum of the largest heights of each row plus spacing
    override fun computeChildrenHeight() = UILayout.maximumChildHeight(children)

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        val w = (width - (children.size - 1) * spacing.get()) / children.size
        val h = height

        UILayout.positionChildrenChildren(children)

        UILayout.positionChildren(children, 0) { index, child ->
            align(child, vec2(index * (w + spacing.get()), 0f), vec2(w, h), alignment.get())
            index + 1
        }
    }
}
