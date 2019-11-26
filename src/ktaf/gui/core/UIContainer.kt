package ktaf.gui.core

/** Add a child to this container. */
fun <Child: UINode> UIContainer<Child>.child(child: Child, fn: Child.() -> Unit = {})
        = addChild(child).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

/** A node with a publicly editable list of children. */
abstract class UIContainer<Child: UINode>: UIParent<Child>() {
    public override val children = super.children

    public override fun <T: Child> addChild(child: T)
            = super.addChild(child)

    public override fun <T: Child> addChild(index: Int, child: T)
            = super.addChild(index, child)

    public override fun <T: Child> removeChild(child: T)
            = super.removeChild(child)

    public override fun clearChildren()
            = super.clearChildren()

}
