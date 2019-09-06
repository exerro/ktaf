package ktaf.ui.layout

import geometry.vec2
import ktaf.core.KTAFValue

class ListLayout(spacing: Spacing = Spacing.SPACE_AFTER): UILayout() {
    val alignment = KTAFValue(0.5f)
    val spacing = KTAFValue(spacing)

    // compute the width for each child where allocated width fills the area
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.fillChildrenWidths(children, widthAllocatedForContent)
    }

    // compute the height for each child where allocated height is non-existent
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        UILayout.setChildrenHeights(children, null)
    }

    // return the largest of the children's widths as the content width
    override fun computeChildrenWidth() = UILayout.maximumChildWidth(children)

    // return the sum of the children's widths plus the fixed spacing
    override fun computeChildrenHeight() = UILayout.sumChildrenHeight(children) + (children.size - 1) * spacing.get().fixed()

    override fun position(width: Float, height: Float) {
        val contentHeight = UILayout.sumChildrenHeight(children)
        val (offset, spacing) = spacing.get().evaluate(height - contentHeight, children.size)

        UILayout.positionChildrenChildren(children)

        UILayout.positionChildren(children, offset) { y, child ->
            UILayout.alignw(child, vec2(0f, y), width, alignment.get())
            y + child.computedHeight + child.margin.get().height + spacing
        }
    }
}
