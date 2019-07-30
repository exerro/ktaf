package ktaf.ui.layout

import ktaf.core.KTAFValue
import ktaf.core.vec2
import kotlin.math.max

class GridLayout(rows: Int = 2, columns: Int = 2): UILayout() {
    val alignment = KTAFValue(vec2(0.5f))
    val spacing = KTAFValue(vec2(0f)) // TODO: use proper spacing
    val columns = KTAFValue(columns)
    val rows = KTAFValue(rows)

    // compute the width for each child where allocated width fills the area divided into `columns` columns
    override fun computeChildrenWidths(widthAllocatedForContent: Float?) {
        UILayout.setChildrenWidths(children, widthAllocatedForContent ?.let { w -> (w - (columns.get() - 1) * spacing.get().x) / columns.get() })
    }

    // compute the height for each child where allocated height fills the area divided into `rows` rows
    override fun computeChildrenHeights(width: Float, heightAllocatedForContent: Float?) {
        UILayout.setChildrenHeights(children, heightAllocatedForContent ?.let { h -> (h - (rows.get() - 1) * spacing.get().y) / rows.get() })
    }

    // return the sum of the largest widths of each column plus spacing
    override fun computeChildrenWidth(): Float {
        // break the children into rows
        val rows = children.chunked(columns.get())
        // compute the transpose of the rows and map child -> child width or 0
        val columnWidths = (0 until columns.get()).map { column -> rows.map { it.getOrNull(column)?.computedWidth ?: 0f } }
        // return the sum of the maximum widths for each column plus appropriate spacing
        return columnWidths.map { it.fold(0f, ::max) } .sum() + spacing.get().x * (columns.get() - 1)
    }

    // return the sum of the largest heights of each row plus spacing
    override fun computeChildrenHeight(): Float {
        // compute the height of each node in each row
        val rowHeights = children.chunked(columns.get()).map { it.map { node -> node.computedHeight } }
        // return the sum of the maximum heights for each row plus appropriate spacing
        return rowHeights.map { it.fold(0f, ::max) } .sum() + spacing.get().y * (rows.get() - 1)
    }

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        val w = (width - (columns.get() - 1) * spacing.get().x) / columns.get()
        val h = (height - (rows.get() - 1) * spacing.get().y) / rows.get()

        UILayout.positionChildrenChildren(children)

        UILayout.positionChildren(children, 0) { index, child ->
            val row = index / columns.get()
            val col = index % columns.get()
            align(child, vec2(col * (w + spacing.get().x), row * (h + spacing.get().y)), vec2(w, h), alignment.get())
            index + 1
        }
    }
}
