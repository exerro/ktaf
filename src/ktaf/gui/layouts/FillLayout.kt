package ktaf.gui.layouts

import geometry.vec2
import ktaf.gui.core.Layout
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment2DProperty

class FillLayout: Layout() {
    val alignment = alignment2DProperty(vec2(0.5f))

    ////////////////////////////////////////////////////////////////////////////

    override fun calculateChildrenWidths(children: List<UINode>, availableWidth: Float) {
        children.forEach { it.calculateWidth(availableWidth) }
    }

    override fun calculateChildrenHeights(children: List<UINode>, availableHeight: Float?) {
        children.forEach { it.calculateHeight(availableHeight) }
    }

    override fun positionChildren(children: List<UINode>, offset: vec2, size: vec2) {
        children.forEach { child ->
            child.position(offset + (size - child.calculatedSize) * alignment.value)
        }
    }
}
