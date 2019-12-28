package ktaf.gui.elements

import geometry.vec2
import ktaf.data.property.MutableProperty
import ktaf.gui.core.Alignment2D
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UIPublicParent
import ktaf.gui.layouts.GridLayout

fun UIContainer.grid(columns: Int, rows: Int, fn: Grid.() -> Unit = {})
        = addChild(Grid(columns, rows)).also(fn)

fun GUIBuilderContext.grid(columns: Int, rows: Int, fn: Grid.() -> Unit = {})
        = Grid(columns, rows).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

open class Grid(columns: Int, rows: Int): UIPublicParent() {
    val columns: MutableProperty<Int>
    val rows: MutableProperty<Int>
    val alignment: MutableProperty<Alignment2D>
    val spacing: MutableProperty<vec2>

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = padding.value.width + spacing.value.x * (columns.value - 1)

    override fun getDefaultHeight(width: Float)
            = padding.value.height + spacing.value.y * (rows.value - 1)

    ////////////////////////////////////////////////////////////////////////////

    override val layout: GridLayout

    init {
        val g = GridLayout(columns, rows)
        layout = g
        this.columns = g.columns
        this.rows = g.rows
        alignment = g.alignment
        spacing = g.spacing
    }
}
