
import ktaf.core.MouseClickEvent
import ktaf.core.application
import ktaf.graphics.DrawContext2D
import ktaf.ui.Hotkey
import ktaf.ui.elements.UIButton
import ktaf.ui.node.UIContainer
import ktaf.ui.layout.*
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene
import org.lwjgl.glfw.GLFW

fun main() = application("Hello world") {
    val context = DrawContext2D(screen)
    val scene = scene(display, context) {
        root.set(UIContainer()) {
            val content = children.add(UIContainer()) {
                padding(Border(16f, 0f))
            }

            val button = children.add(UIButton("Add button 6")) {
                width(200f)
                height(50f)
            }

            fun UIContainer.addButton() {
                val container = this

                children.add(UIButton("Button ${container.children.size}")) {
                    width(80f)
                    height(30f)
                    hotkeys.add(Hotkey(GLFW.GLFW_KEY_0 + container.children.size))

                    onClick { event -> when (event) {
                        is MouseClickEvent -> parent(null)
                        else -> this.height(Math.random().toFloat() * 20f + 20f)
                    } }

                    container.children.connectChanged { text("Button ${container.children.indexOf(this) + 1}") }
                }
            }

            content.run {
                (1 .. 5).map { content.addButton() }

                layout(FlowLayout()) {
                    horizontalSpacing(Spacing.SPACE_EVENLY)
                    verticalSpacing(Spacing.fixed(50f) then Spacing.align(0.3f))
//                    spacing(Spacing.fixed(20f) then Spacing.SPACE_BEFORE)
//                    spacing(vec2(20f, 10f))
//                    columns(2)
//                    rows(6)
                }

//                layout(GridLayout()) {
//                    columns(2)
//                    rows(6)
//                }
            }

            button.run {
                content.children.connectChanged { button.text("Add button ${content.children.size + 1}") }
                hotkeys.add(Hotkey(GLFW.GLFW_KEY_A))
                onClick { content.addButton() }
            }

            layout(AreaLayout()) {
                areas {
                    split(50.pc()) { labels("l", "r") }
                    area("r") {
                        vsplit(50.pc()) { labels("t", "b") }
                    }
                }
                elem(content, "l")
                elem(button, "t")
            }
        }
    }

    scene.attachCallbacks(this)
}
