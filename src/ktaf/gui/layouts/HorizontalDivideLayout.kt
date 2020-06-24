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

class HorizontalDivideLayout(val partitions: List<Value<Ratio>>): Layout() {
    val alignment = alignment2DProperty(vec2_zero)
    val spacing = mutableProperty(0f)

    constructor(vararg partitions: Ratio): this(partitions.map { const(it) })

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float) {
        val widths = calculateWidths(children.size, availableWidth - spacing.value * (children.size - 1))
        children.forEachIndexed { i, child -> child.calculateWidth(widths[i]) }
    }

    override fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?) {
        children.forEach { it.calculateHeight(availableHeight) }
    }

    override fun positionChildren(children: List<UINode>, offset: vec2, size: vec2) {
        val s = size - vec2(spacing.value * (children.size - 1), 0f)
        val widths = calculateWidths(children.size, s.x)
        var p = offset
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

    private fun calculateWidths(n: Int, size: Float) = partitions.map { it.value.apply(size) } .let { p ->
        val rem = n - partitions.size
        val total = size - p.sum()

        p.take(n) + (1 .. rem).map { total / rem }
    }
}
