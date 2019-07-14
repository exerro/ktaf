import ktaf.core.application
import ktaf.graphics.DrawContext2D
import ktaf.ui.Hotkey
import ktaf.ui.elements.UIButton
import ktaf.ui.node.UIContainer
import ktaf.ui.elements.UIView
import ktaf.ui.layout.AreaLayout
import ktaf.ui.layout.HDivLayout
import ktaf.ui.layout.UILayout
import ktaf.ui.layout.px
import ktaf.ui.node.UINode
import ktaf.ui.node.nextChild
import ktaf.ui.node.previousChild
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene
import org.lwjgl.glfw.GLFW

fun main() = application("UI Graphics") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context) {
        root(UIContainer()) {
            val buttons = children.add(UIContainer()) { layout(HDivLayout()) }
            val content = children.add(UIView()) { horizontal() }

            fun addSection(title: String, node: UINode) {
                buttons.children.add(UIButton(title)) { onClick { content.show(node) } }
                content.children.add(node)
            }

            addSection("Elements Demo", elementsDemo())
            addSection("Position Demo", positionDemo())

            content.hotkeys.add(Hotkey(GLFW.GLFW_KEY_LEFT))
            content.hotkeys.add(Hotkey(GLFW.GLFW_KEY_RIGHT))

            content.onKeyPress {
                when (it.key) {
                    GLFW.GLFW_KEY_LEFT -> content.active.get()?.previousChild() ?.let { content.show(it) }
                    GLFW.GLFW_KEY_RIGHT -> content.active.get()?.nextChild() ?.let { content.show(it) }
                }
            }

            layout(AreaLayout()) {
                areas {
                    vsplit(64.px()) { labels("header", "content") }
                    elem(buttons, "header")
                    elem(content, "content")
                }
            }
        }
    }

    scene.attachCallbacks(this)
}

inline fun <reified T: UILayout> setLayout(element: UIContainer, create: () -> T, fn: (T) -> Unit) {
    when (val layout = element.layout.get()) {
        is T -> { fn(layout) }
        else -> { fn(element.layout(create())); fn(element.layout.get() as T) }
    }
}
