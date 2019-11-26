package ktaf.gui.elements

import geometry.vec2
import ktaf.data.ObservableList
import ktaf.gui.core.*
import kotlin.math.max

fun <T, Child: UINode> UIContainer<UINode>.list(items: ObservableList<T>, fn: GUIBuilderContext.(T) -> Child)
        = addChild(ListNode(items, fn))

fun <T, Child: UINode> GUIBuilderContext.list(items: ObservableList<T>, fn: GUIBuilderContext.(T) -> Child)
        = ListNode(items, fn)

//////////////////////////////////////////////////////////////////////////////////////////

// TODO: add spacing
class ListNode<T, Child: UINode>(
        val items: ObservableList<T>,
        private val fn: GUIBuilderContext.(T) -> Child
): UIParent<Child>() {
    val alignment = alignment1DProperty(0.5f)

    override fun getDefaultWidth()
            = children.map { it.calculatedSize.x } .fold(0f, ::max)

    override fun getDefaultHeight(width: Float)
            = children.map { it.calculatedSize.y } .sum()

    override fun calculateChildrenWidths(availableWidth: Float) {
        val w = (width.value ?: availableWidth) - padding.value.width
        children.forEach { it.calculateWidth(w) }
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        children.forEach { it.calculateHeight(null) }
    }

    override fun positionChildren() {
        var p = calculatedPosition + padding.value.topLeft
        val s = calculatedSize - padding.value.size
        val w = s.x

        children.forEach { child ->
            // TODO: fix relative position value
            child.position(p + vec2((w - child.calculatedSize.x) * alignment.value, 0f))
            p += vec2(0f, child.calculatedSize.y)
        }
    }

    init {
        items.forEach { addChild(fn(GUIBuilder, it)) }
        items.onItemAdded.connect { addChild(it, fn(GUIBuilder, items[it])) }
        items.onItemRemoved.connect { removeChild(children[it]) }
    }
}
