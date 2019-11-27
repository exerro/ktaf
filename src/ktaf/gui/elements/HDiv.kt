package ktaf.gui.elements

import geometry.*
import ktaf.data.Ratio
import ktaf.data.property.AnimatedProperty
import ktaf.data.property.mutableProperty
import ktaf.data.ratioAnimatedProperty
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment2DProperty

fun UIContainer.hdiv(vararg partitions: Ratio, fn: HDiv.() -> Unit = {})
        = addChild(HDiv(*partitions)).also(fn)

fun GUIBuilderContext.hdiv(vararg partitions: Ratio, fn: HDiv.() -> Unit = {})
        = HDiv(*partitions).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class HDiv(
        val partitions: List<AnimatedProperty<Ratio>>
): UIContainer() {
    val alignment = alignment2DProperty(vec2_zero)
    val spacing = mutableProperty(0f)

    constructor(vararg partitions: Ratio): this(partitions.map { ratioAnimatedProperty(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth() = childrenWidthTotal + paddingAndSpacing
    override fun getDefaultHeight(width: Float) = childrenHeightMaximum + padding.value.height

    override fun calculateChildrenWidths(availableWidth: Float) {
        val widths = calculateWidths((width.value ?: availableWidth) - paddingAndSpacing)
        children.forEachIndexed { i, child -> child.calculateWidth(widths[i]) }
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        val h = (height.value ?: availableHeight) ?.let { it - padding.value.height }
        children.forEach { it.calculateHeight(h) }
    }

    override fun positionChildren() {
        val s = calculatedSize - vec2(paddingAndSpacing, padding.value.height)
        val widths = calculateWidths(s.x)
        var p = calculatedPosition + padding.value.topLeft
        val sp = spacing.value
        val h = s.y
        val a = alignment.value

        children.forEachIndexed { i, child ->
            val w = widths[i]
            child.position(p + (vec2(w, h) - child.calculatedSize) * a)
            p += vec2(w + sp, 0f)
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun calculateWidths(size: Float) = partitions.map { it.value.apply(size) } .let { p ->
        val rem = children.size - partitions.size
        val total = size - p.sum()

        p.take(children.size) + (1 .. rem).map { total / rem }
    }

    init {
        partitions.forEach(this::addAnimatedProperty)
    }

    ////////////////////////////////////////////////////////////////////////////

    val paddingAndSpacing
        get() = padding.value.width + spacing.value * (children.size - 1)
}
