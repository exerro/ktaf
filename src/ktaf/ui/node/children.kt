package ktaf.ui.node

fun UINode.orderedChildren(): List<UINode> {
    return ordering.get().apply(children)
}
