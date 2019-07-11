package ktaf.ui

import ktaf.graphics.DrawContext2D
import lwjglkt.GLFWDisplay
import ktaf.ui.elements.UIContainer
import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.UILayout
import ktaf.ui.node.UINode
import ktaf.ui.scene.UIScene

fun scene(display: GLFWDisplay, context: DrawContext2D, init: UIScene.() -> Unit = {}): UIScene {
    val root = UIScene(display, context)
    init(root)
    return root
}

fun <N: UINode> N.list(init: UIContainer.() -> Unit): UIContainer {
    val child = children.add(UIContainer(), {
        layout(ListLayout())
    })
    init(child)
    return child
}
