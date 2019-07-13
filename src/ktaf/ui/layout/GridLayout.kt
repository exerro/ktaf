package ktaf.ui.layout

import ktaf.core.KTAFMutableValue
import ktaf.core.vec2
import kotlin.math.max

class GridLayout(rows: Int = 2, columns: Int = 2): UILayout() {
    val alignment = KTAFMutableValue(vec2(0.5f))
    val spacing = KTAFMutableValue(vec2(0f)) // TODO: use proper spacing
    val columns = KTAFMutableValue(columns)
    val rows = KTAFMutableValue(rows)

    override fun computeChildrenWidth(widthAllocatedForContent: Float): Lazy<Float> {
        // compute the width for each child where allocated width fills the area divided into `columns` columns
        UILayout.setChildrenWidths(children, (widthAllocatedForContent - (columns.get() - 1) * spacing.get().x) / columns.get())
        // return the sum of the largest widths of each column plus spacing
        return lazy {
            // break the children into rows
            val rows = children.chunked(columns.get())
            // compute the transpose of the rows and map child -> child width or 0
            val columnWidths = (0 until columns.get()).map { column -> rows.map { it.getOrNull(column)?.computedWidthInternal ?: 0f } }
            // return the sum of the maximum widths for each column plus appropriate spacing
            columnWidths.map { it.fold(0f, ::max) } .sum() + spacing.get().x * (columns.get() - 1)
        }
    }

    override fun computeChildrenHeight(width: Float, heightAllocatedForContent: Float?): Lazy<Float> {
        // compute the height for each child where allocated height fills the area divided into `rows` rows
        UILayout.setChildrenHeights(children, heightAllocatedForContent?.let { h -> (h - (rows.get() - 1) * spacing.get().y) / rows.get() })
        // return the sum of the largest heights of each row plus spacing
        return lazy {
            // compute the height of each node in each row
            val rowHeights = children.chunked(columns.get()).map { it.map { node -> node.computedHeightInternal } }
            // return the sum of the maximum heights for each row plus appropriate spacing
            rowHeights.map { it.fold(0f, ::max) } .sum() + spacing.get().y * (rows.get() - 1)
        }
    }

    // TODO: this process needs better documenting
    override fun position(width: Float, height: Float) {
        val w = (width - (columns.get() - 1) * spacing.get().x) / columns.get()
        val h = (height - (rows.get() - 1) * spacing.get().y) / rows.get()

        children.forEach { it.layout.get().computePositionForChildren(it) }

        UILayout.positionChildren(children, 0) { index, child ->
            val row = index / columns.get()
            val col = index % columns.get()
            align(child, vec2(col * (w + spacing.get().x), row * (h + spacing.get().y)), vec2(w, h), alignment.get())
            index + 1
        }
    }
}
