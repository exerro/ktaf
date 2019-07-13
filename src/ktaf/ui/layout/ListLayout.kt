package ktaf.ui.layout

import ktaf.core.KTAFMutableValue
import ktaf.core.vec2

class ListLayout : UILayout() {
    val alignment = KTAFMutableValue(0.5f)
    val spacing = KTAFMutableValue(Spacing.SPACE_AFTER)

    override fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float> {
        // compute the width for each child where allocated width fills the area
        UILayout.fillChildrenWidths(children, widthAllocatedForContent)
        // return the largest of the children's widths as the content width
        return lazy { UILayout.maximumChildWidth(children) }
    }

    override fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float> {
        // compute the height for each child where allocated height is non-existent
        UILayout.setChildrenHeights(children, null)
        // return the sum of the children's widths plus the fixed spacing
        return lazy { UILayout.sumChildrenHeight(children) + (children.size - 1) * spacing.get().fixed() }
    }

    override fun position(width: Float, height: Float) {
        val contentHeight = UILayout.sumChildrenHeight(children)
        val (offset, spacing) = spacing.get().evaluate(height - contentHeight, children.size)

        children.forEach { it.layout.get().computePositionForChildren(it) }

        UILayout.positionChildren(children, offset) { y, child ->
            UILayout.alignw(child, vec2(0f, y), width, alignment.get())
            y + child.computedHeightInternal + child.margin.get().height + spacing
        }
    }
}
