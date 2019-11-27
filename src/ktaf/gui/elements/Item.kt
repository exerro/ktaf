package ktaf.gui.elements

import ktaf.data.Value
import ktaf.gui.core.UINode
import ktaf.gui.core.UIParent

class Item<T>(
        val item: Value<T>,
        private val fn: (T) -> UINode
): UIParent() {
    override fun getDefaultWidth() = children[0].getDefaultWidth()
    override fun getDefaultHeight(width: Float) = children[0].getDefaultHeight(width)

    override fun calculateChildrenWidths(availableWidth: Float) {
        children[0].calculateWidth(availableWidth)
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        children[0].calculateHeight(availableHeight)
    }

    override fun positionChildren() {
        children[0].position(calculatedPosition)
    }

    ////////////////////////////////////////////////////////////////////////////

    init {
        addChild(fn(item.value))

        item.onChangeEvent?.connect {
            clearChildren()
            addChild(fn(item.value))
        }
    }
}
