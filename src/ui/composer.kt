package ui

import graphics.DrawContext2D

fun scene(context: DrawContext2D, init: UIScene.() -> Unit): UIScene {
    val root = UIScene(context)
    init(root)
    return root
}

fun <N: UINode> UIScene.root(node: N, init: N.() -> Unit): N {
    addRoot(node)
    init(node)
    return node
}

fun <N: UINode, L: UILayout> N.layout(layout: L, init: L.() -> Unit = {}): L {
    init(layout)
    this.layout = layout
    return layout
}

fun <N: UINode> N.list(init: UIContainer.() -> Unit): UIContainer {
    val child = addChild(UIContainer()) {
        layout(ListLayout())
    }
    init(child)
    return child
}
