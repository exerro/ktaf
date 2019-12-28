package ktaf.gui.layouts

import geometry.vec2
import geometry.vec2_zero
import ktaf.data.property.mutableProperty
import ktaf.gui.core.Layout
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment2DProperty

class GridLayout(columns: Int, rows: Int): Layout() {
    val columns = mutableProperty(columns)
    val rows = mutableProperty(rows)
    val alignment = alignment2DProperty(vec2(0.5f))
    val spacing = mutableProperty(vec2_zero)

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float) {
        val w = (availableWidth - spacing.value.x * (columns.value - 1)) / columns.value
        children.forEach { it.calculateWidth(w) }
    }

    override fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?) {
        val h = (availableHeight ?.let { it - spacing.value.y * (rows.value - 1) })?.let { it / rows.value }
        children.forEach { it.calculateHeight(h) }
    }

    override fun positionChildren(children: List<UINode>, offset: vec2, size: vec2) {
        val sx = size.x - spacing.value.x * (columns.value - 1)
        val sy = size.y - spacing.value.y * (rows.value - 1)
        val s = vec2(sx, sy)
        val a = alignment.value
        val ca = s / vec2(columns.value.toFloat(), rows.value.toFloat())
        val gs = ca + spacing.value
        var gc = vec2(0f, -1f)

        children.forEachIndexed { i, child ->
            if (i % columns.value == 0) {
                gc = vec2(0f, gc.y + 1)
            }

            child.position(offset + gs * gc + (ca - child.calculatedSize) * a)

            gc += vec2(1f, 0f)
        }
    }
}
