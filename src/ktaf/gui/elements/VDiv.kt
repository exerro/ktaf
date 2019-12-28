package ktaf.gui.elements

import ktaf.data.Ratio
import ktaf.data.Value
import ktaf.data.property.MutableProperty
import ktaf.data.ratioAnimatedProperty
import ktaf.gui.core.Alignment2D
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UIPublicParent
import ktaf.gui.layouts.VerticalDivideLayout
import kotlin.collections.List

fun UIContainer.vdiv(vararg partitions: Ratio, fn: VDiv.() -> Unit = {})
        = addChild(VDiv(*partitions)).also(fn)

fun GUIBuilderContext.vdiv(vararg partitions: Ratio, fn: VDiv.() -> Unit = {})
        = VDiv(*partitions).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

open class VDiv(
        partitions: List<Value<Ratio>>
): UIPublicParent() {
    val alignment: MutableProperty<Alignment2D>
    val spacing: MutableProperty<Float>

    constructor(vararg partitions: Ratio): this(partitions.map { ratioAnimatedProperty(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = childrenWidthMaximum + padding.value.width

    override fun getDefaultHeight(width: Float)
            = childrenHeightTotal + padding.value.height + spacing.value * (children.size - 1)

    ////////////////////////////////////////////////////////////////////////////

    override val layout: VerticalDivideLayout

    init {
        val l = VerticalDivideLayout(partitions)
        layout = l
        alignment = l.alignment
        spacing = l.spacing
    }
}
