package ktaf.gui.layouts

import geometry.vec2
import ktaf.gui.core.Layout
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment1DProperty
import ktaf.gui.core.spacingProperty

class VerticalListLayout: Layout() {
    val alignment = alignment1DProperty(0.5f)
    val spacing = spacingProperty()

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float) {
        children.forEach { it.calculateWidth(availableWidth) }
    }

    override fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?) {
        children.forEach { it.calculateHeight(null) }
    }

    override fun positionChildren(children: List<UINode>, offset: vec2, size: vec2) {
        val childrenHeights = children.map { it.calculatedSize.y } .sum()
        val (o, spacing) = spacing.value.apply(size.y - childrenHeights, children.size)
        var p = offset + vec2(0f, o)
        val w = size.x

        children.forEach { child ->
            child.position(p + vec2((w - child.calculatedSize.x) * alignment.value, 0f))
            p += vec2(0f, child.calculatedSize.y + spacing)
        }
    }
}
