package ktaf.gui.core

/** Add a child to this container. */
fun <Child: UINode> UIContainer.child(child: Child, fn: Child.() -> Unit = {})
        = addChild(child).also(fn)

/** Add a child to this container. */
fun <C: UIContainer> C.children(fn: C.() -> Unit = {})
        = fn(this)

//////////////////////////////////////////////////////////////////////////////////////////

/** A node with a publicly editable list of children. */
interface UIContainer {
    val children: List<UINode>

    fun <T: UINode> addChild(child: T): T
    fun <T: UINode> addChild(index: Int, child: T): T
    fun <T: UINode> removeChild(child: T): T
    fun clearChildren()
}
