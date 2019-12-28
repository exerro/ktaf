package ktaf.gui.elements

import ktaf.data.property.MutableProperty
import ktaf.gui.core.*
import ktaf.gui.layouts.VerticalListLayout
import kotlin.math.max

fun UIContainer.hstack(fn: HStack.() -> Unit = {})
        = addChild(HStack()).also(fn)

fun GUIBuilderContext.hstack(fn: HStack.() -> Unit = {})
        = HStack().also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class HStack: UIPublicParent() {
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
    }
}
