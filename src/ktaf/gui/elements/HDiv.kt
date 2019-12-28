package ktaf.gui.elements

import ktaf.data.Ratio
import ktaf.data.Value
import ktaf.data.property.MutableProperty
import ktaf.data.property.const
import ktaf.gui.core.Alignment2D
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UIPublicParent
import ktaf.gui.layouts.HorizontalDivideLayout
import kotlin.collections.List

fun UIContainer.hdiv(vararg partitions: Ratio, fn: HDiv.() -> Unit = {})
        = addChild(HDiv(*partitions)).also(fn)

fun GUIBuilderContext.hdiv(vararg partitions: Ratio, fn: HDiv.() -> Unit = {})
        = HDiv(*partitions).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

open class HDiv(
        partitions: List<Value<Ratio>>
): UIPublicParent() {
    val alignment: MutableProperty<Alignment2D>
    val spacing: MutableProperty<Float>

    constructor(vararg partitions: Ratio): this(partitions.map { const(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = childrenWidthTotal + padding.value.width + spacing.value * (children.size - 1)

    override fun getDefaultHeight(width: Float)
            = childrenHeightMaximum + padding.value.height

    ////////////////////////////////////////////////////////////////////////////

    override val layout: HorizontalDivideLayout

    init {
        val h = HorizontalDivideLayout(partitions)
        layout = h
        alignment = h.alignment
        spacing = h.spacing
    }
}
