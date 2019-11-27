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
import kotlin.math.max

fun UIContainer.vdiv(vararg partitions: Ratio, fn: VDiv.() -> Unit = {})
        = addChild(VDiv(*partitions)).also(fn)

fun GUIBuilderContext.vdiv(vararg partitions: Ratio, fn: VDiv.() -> Unit = {})
        = VDiv(*partitions).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class VDiv(
        val partitions: List<AnimatedProperty<Ratio>>
): UIContainer() {
    val alignment = alignment2DProperty(vec2_zero)
    val spacing = mutableProperty(0f)

    constructor(vararg partitions: Ratio): this(partitions.map { ratioAnimatedProperty(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth() = childrenWidthMaximum + padding.value.width
    override fun getDefaultHeight(width: Float) = childrenHeightTotal + paddingAndSpacing

    override fun calculateChildrenWidths(availableWidth: Float) {
        val w = (width.value ?: availableWidth) - padding.value.width
        children.forEach { it.calculateWidth(w) }
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        val heights = calculateHeights((height.value ?: availableHeight) ?.let { it - paddingAndSpacing} ?: 0f)
        children.forEachIndexed { i, child -> child.calculateHeight(heights[i]) }
    }

    override fun positionChildren() {
        val s = calculatedSize - vec2(padding.value.width, paddingAndSpacing)
        val heights = calculateHeights(s.y)
        var p = calculatedPosition + padding.value.topLeft
        val sp = spacing.value
        val w = s.x
        val a = alignment.value

        children.forEachIndexed { i, child ->
            val h = heights[i]
            child.position(p + (vec2(w, h) - child.calculatedSize) * a)
            p += vec2(0f, h + sp)
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun calculateHeights(size: Float) = partitions.map { it.value.apply(size) } .let { p ->
        val rem = children.size - partitions.size
        val total = size - p.sum()

        p.take(children.size) + (1 .. rem).map { total / rem }
    }

    ////////////////////////////////////////////////////////////////////////////

    init {
        partitions.forEach(this::addAnimatedProperty)
    }

    ////////////////////////////////////////////////////////////////////////////

    val paddingAndSpacing
        get() = padding.value.height + spacing.value * (children.size - 1)
}
