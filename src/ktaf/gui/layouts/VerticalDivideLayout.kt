package ktaf.gui.layouts

import geometry.vec2
import geometry.vec2_zero
import ktaf.data.Ratio
import ktaf.data.Value
import ktaf.data.property.const
import ktaf.data.property.mutableProperty
import ktaf.gui.core.Layout
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment2DProperty

class VerticalDivideLayout(private val partitions: List<Value<Ratio>>): Layout() {
    val alignment = alignment2DProperty(vec2_zero)
    val spacing = mutableProperty(0f)

    constructor(vararg partitions: Ratio): this(partitions.map { const(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float) {
        children.forEach { it.calculateWidth(availableWidth) }
    }

    override fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?) {
        val heights = calculateHeights(children.size, availableHeight ?.let { it - spacing.value * (children.size - 1) } ?: 0f)
        children.forEachIndexed { i, child -> child.calculateHeight(heights[i]) }
    }

    override fun positionChildren(children: List<UINode>, offset: vec2, size: vec2) {
        val s = size - vec2(spacing.value * (children.size - 1), 0f)
        val heights = calculateHeights(children.size, s.y)
        var p = offset
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

    private fun calculateHeights(n: Int, size: Float) = partitions.map { it.value.apply(size) } .let { p ->
        val rem = n - partitions.size
        val total = size - p.sum()

        p.take(n) + (1 .. rem).map { total / rem }
    }
}
