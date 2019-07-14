package ktaf.ui.layout

import ktaf.core.KTAFValue
import ktaf.core.vec2
import ktaf.ui.node.UINode

class FlowLayout: UILayout() {
    val horizontalSpacing = KTAFValue(Spacing.SPACE_AFTER)
    val verticalSpacing = KTAFValue(Spacing.SPACE_AFTER)
    val verticalAlignment = KTAFValue(0f)

    override fun begin(children: List<UINode>) {
        super.begin(children)
        rows = mutableListOf(mutableListOf())
    }

    // compute the width for each child where allocated width fills the area
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.setChildrenWidths(children, null)
    }

    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        // compute the height for each child where allocated height is non-existent
        UILayout.setChildrenHeights(children, null)

        // TODO: this process needs better documenting
        children.fold(0f) { x, child ->
            val w = child.margin.get().width + child.computedWidthInternal

            if (x + w > width) {
                rows.add(mutableListOf(child))
                w + horizontalSpacing.get().fixed
            }
            else {
                rows.last().add(child)
                x + w + horizontalSpacing.get().fixed
            }
        }
    }

    // return the largest of the children's widths as the content width
    override fun computeChildrenWidth() = UILayout.sumChildrenWidth(children) + children.size * horizontalSpacing.get().fixed()

    // TODO: this process needs documenting
    override fun computeChildrenHeight() = rows.map { row -> UILayout.maximumChildHeight(row) } .sum() + (rows.size - 1) * verticalSpacing.get().fixed()

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        val contentHeight = rows.map { row -> UILayout.maximumChildHeight(row) } .sum()
        val (yOffset, ySpacing) = verticalSpacing.get().evaluate(height - contentHeight, rows.size)

        UILayout.positionChildrenChildren(children)

        rows.fold(yOffset) { y, row ->
            val rowWidth = UILayout.sumChildrenWidth(row)
            val rowHeight = UILayout.maximumChildHeight(row)
            val (xOffset, xSpacing) = horizontalSpacing.get().evaluate(width - rowWidth, row.size)

            row.fold(xOffset) { x, child ->
                UILayout.alignh(child, vec2(x, y), rowHeight, verticalAlignment.get())
                x + xSpacing + child.margin.get().width + child.computedWidthInternal
            }

            y + ySpacing + rowHeight
        }
    }

    private lateinit var rows: MutableList<MutableList<UINode>>
}
