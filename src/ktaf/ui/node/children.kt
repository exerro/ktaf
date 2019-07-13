package ktaf.ui.node

fun UINode.orderedChildren(): List<UINode>
        = ordering.get().apply(children)

fun UINode.previousChild()
        = parent.get() ?.let { it.children.getOrNull(it.children.indexOf(this) - 1) }

fun UINode.nextChild()
        = parent.get() ?.let { it.children.getOrNull(it.children.indexOf(this) + 1) }
