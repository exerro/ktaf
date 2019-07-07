package ktaf.ui

import ktaf.graphics.DrawContext2D
import lwjglkt.GLFWDisplay
import ktaf.ui.elements.UIContainer

fun scene(display: GLFWDisplay, context: DrawContext2D, init: UIScene.() -> Unit = {}): UIScene {
    val root = UIScene(display, context)
    init(root)
    return root
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
