package ktaf.gui.elements

import ktaf.data.property.MutableProperty
import ktaf.gui.core.Alignment2D
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import ktaf.gui.layouts.FillLayout
import java.lang.Float.max

fun UIContainer.stack(vararg children: UINode, fn: Stack.() -> Unit = {})
        = addChild(Stack(*children)).also(fn)

fun GUIBuilderContext.stack(vararg children: UINode, fn: Stack.() -> Unit = {})
        = Stack(*children).also(fn)

fun <Child: UINode> UIContainer.stack(child: Child, vararg children: Child, fn: Stack.() -> Unit = {})
        = addChild(Stack(*children)).also(fn)

fun <Child: UINode> GUIBuilderContext.stack(child: Child, vararg children: Child, fn: Stack.() -> Unit = {})
        = Stack(*children).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

open class Stack(children: List<UINode>): UIContainer() {
    val alignment: MutableProperty<Alignment2D>

    constructor(vararg children: UINode): this(children.toList())

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth()
            = children.map { it.calculatedSize.x } .fold(0f, ::max) + padding.value.width

    override fun getDefaultHeight(width: Float)
            = children.map { it.calculatedSize.y } .fold(0f, ::max) + padding.value.height

    ////////////////////////////////////////////////////////////////////////////

    override val layout: FillLayout

    init {
        val f = FillLayout()
        layout = f
        alignment = f.alignment
        children.forEach { addChild(it) }
    }
}
