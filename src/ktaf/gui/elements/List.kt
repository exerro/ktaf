package ktaf.gui.elements

import ktaf.data.ObservableList
import ktaf.data.property.MutableProperty
import ktaf.gui.core.*
import ktaf.gui.layouts.VerticalListLayout
import kotlin.math.max

fun <T> UIContainer.list(items: ObservableList<T>, fn: GUIBuilderContext.(T) -> UINode)
        = addChild(List(items, fn))

fun <T> GUIBuilderContext.list(items: ObservableList<T>, fn: GUIBuilderContext.(T) -> UINode)
        = List(items, fn)

//////////////////////////////////////////////////////////////////////////////////////////

class List<T>(
        val items: ObservableList<T>,
        private val fn: GUIBuilderContext.(T) -> UINode
): UIParent() {
    val alignment: MutableProperty<Alignment1D>
    val spacing: MutableProperty<Spacing>

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = children.map { it.calculatedSize.x } .fold(0f, ::max)

    override fun getDefaultHeight(width: Float)
            = children.map { it.calculatedSize.y } .sum() +
              spacing.value.minimum(children.size)

    ////////////////////////////////////////////////////////////////////////////

    override val layout: VerticalListLayout

    init {
        val l = VerticalListLayout()
        layout = l
        alignment = l.alignment
        spacing = l.spacing
        items.forEach { addChild(fn(GUIBuilder, it)) }
        items.onItemAdded.connect { addChild(it, fn(GUIBuilder, items[it])) }
        items.onItemRemoved.connect { removeChild(children[it]) }
    }
}
