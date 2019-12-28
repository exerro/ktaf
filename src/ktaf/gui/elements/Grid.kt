package ktaf.gui.elements

import geometry.*
import ktaf.data.property.mutableProperty
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.alignment2DProperty

fun UIContainer.grid(columns: Int, rows: Int, fn: Grid.() -> Unit = {})
        = addChild(Grid(columns, rows)).also(fn)

fun GUIBuilderContext.grid(columns: Int, rows: Int, fn: Grid.() -> Unit = {})
        = Grid(columns, rows).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

open class Grid(columns: Int, rows: Int): UIContainer() {
    val columns = mutableProperty(columns)
    val rows = mutableProperty(rows)
    val alignment = alignment2DProperty(vec2(0.5f))
    val spacing = mutableProperty(vec2_zero)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = padding.value.width + spacing.value.x * (columns.value - 1)

    override fun getDefaultHeight(width: Float)
            = padding.value.height + spacing.value.y * (rows.value - 1)

    override fun calculateChildrenWidths(availableWidth: Float) {
        val w = (availableWidth - padding.value.width - spacing.value.x * (columns.value - 1)) / columns.value
        children.forEach { it.calculateWidth(w) }
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        val h = ((availableHeight ?: 0f) - padding.value.height - spacing.value.y * (rows.value - 1)) / rows.value
        children.forEach { it.calculateHeight(h) }
    }

    override fun positionChildren() {
        val sx = calculatedSize.x - padding.value.width - spacing.value.x * (columns.value - 1)
        val sy = calculatedSize.y - padding.value.height - spacing.value.y * (rows.value - 1)
        val s = vec2(sx, sy)
        val p = calculatedPosition + padding.value.topLeft
        val a = alignment.value
        val ca = s / vec2(columns.value.toFloat(), rows.value.toFloat())
        val gs = ca + spacing.value
        var gc = vec2(0f, -1f)

        children.forEachIndexed { i, child ->
            if (i % columns.value == 0) {
                gc = vec2(0f, gc.y + 1)
            }

            child.position(p + gs * gc + (ca - child.calculatedSize) * a)

            gc += vec2(1f, 0f)
        }
    }
}
