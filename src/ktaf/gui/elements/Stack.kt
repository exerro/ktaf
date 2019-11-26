package ktaf.gui.elements

import geometry.vec2
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import ktaf.gui.core.alignment2DProperty
import java.lang.Float.max

fun UIContainer<UINode>.stack(vararg children: UINode, fn: Stack<UINode>.() -> Unit = {})
        = addChild(Stack(*children)).also(fn)

fun GUIBuilderContext.stack(vararg children: UINode, fn: Stack<UINode>.() -> Unit = {})
        = Stack(*children).also(fn)

fun <Child: UINode> UIContainer<UINode>.stack(child: Child, vararg children: Child, fn: Stack<Child>.() -> Unit = {})
        = addChild(Stack(*children)).also(fn)

fun <Child: UINode> GUIBuilderContext.stack(child: Child, vararg children: Child, fn: Stack<Child>.() -> Unit = {})
        = Stack(*children).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class Stack<Child: UINode>(children: List<Child>): UIContainer<Child>() {
    val alignment = alignment2DProperty(vec2(0.5f, 0.5f))

    constructor(vararg children: Child): this(children.toList())

    override fun getDefaultWidth()
            = children.map { it.calculatedSize.x } .fold(0f, ::max) + padding.value.width

    override fun getDefaultHeight(width: Float)
            = children.map { it.calculatedSize.y } .fold(0f, ::max) + padding.value.height

    override fun calculateChildrenWidths(availableWidth: Float) {
        val w = (width.value ?: availableWidth) - padding.value.width
        children.forEach { it.calculateWidth(w) }
    }

    override fun calculateChildrenHeights(availableHeight: Float?) {
        val h = (height.value ?: availableHeight) ?.let { it - padding.value.height }
        children.forEach { it.calculateHeight(h) }
    }

    override fun positionChildren() {
        val p = calculatedPosition + padding.value.topLeft
        val s = calculatedSize - padding.value.size

        children.forEach { child ->
            child.position(p + (s - child.calculatedSize) * alignment.value)
        }
    }

    init {
        children.forEach { addChild(it) }
    }
}
