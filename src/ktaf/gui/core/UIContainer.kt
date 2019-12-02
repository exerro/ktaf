package ktaf.gui.core

/** Add a child to this container. */
fun <Child: UINode> UIContainer.child(child: Child, fn: Child.() -> Unit = {})
        = addChild(child).also(fn)

/** Add a child to this container. */
fun <C: UIContainer> C.children(fn: C.() -> Unit = {})
        = fn(this)

//////////////////////////////////////////////////////////////////////////////////////////

/** A node with a publicly editable list of children. */
abstract class UIContainer: UIParent() {
    public override val children = super.children

    public override fun <T: UINode> addChild(child: T)
            = super.addChild(child)

    public override fun <T: UINode> addChild(index: Int, child: T)
            = super.addChild(index, child)

    public override fun <T: UINode> removeChild(child: T)
            = super.removeChild(child)

    public override fun clearChildren()
            = super.clearChildren()

}
