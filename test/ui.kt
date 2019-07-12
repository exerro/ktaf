
import ktaf.core.application
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.ui.layout.Border
import ktaf.ui.Hotkey
import ktaf.ui.UIMouseClickEvent
import ktaf.ui.elements.UIButton
import ktaf.ui.elements.UIContainer
import ktaf.ui.layout.FillLayout
//import ktaf.ui.layout.FreeLayout
//import ktaf.ui.layout.ListLayout
import ktaf.ui.layout.Spacing2
//import ktaf.ui.layout.elem
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene
import org.lwjgl.glfw.GLFW

fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context) {
        root.set(UIContainer()) {
            val content = children.add(UIContainer()) {
                width(100f)
                height(400f)
                padding(Border(16f, 0f))

                layout(FillLayout()) {
                    alignment(vec2(0f))
                }
            }

            val button = children.add(UIButton("Add button 6")) {
                width(200f)
                height(50f)
            }

            fun UIContainer.addButton() {
                val container = this

                children.add(UIButton("Button ${container.children.size}")) {
                    height(30f)
                    hotkeys.add(Hotkey(GLFW.GLFW_KEY_0 + container.children.size))

                    onClick { event -> when (event) {
                        is UIMouseClickEvent -> parent(null)
                        else -> this.height(Math.random().toFloat() * 20f + 20f)
                    } }

                    container.children.connectChanged { text("Button ${container.children.indexOf(this) + 1}") }
                }
            }

            content.run {
                (1 .. 5).map { content.addButton() }

//                layout(ListLayout()) {
//                    spacing(Spacing2.fixed(20f) then Spacing2.SPACE_BEFORE)
//                }

                layout(FillLayout()) {
                    alignment(vec2(0.5f))
                }
            }

            button.run {
                content.children.connectChanged { button.text("Add button ${content.children.size + 1}") }
                hotkeys.add(Hotkey(GLFW.GLFW_KEY_A))
                onClick { content.addButton() }
            }

            layout(FillLayout()) {
                alignment(vec2(0.5f))
            }

//            layout(FreeLayout()) {
//                elem(content) {
//                    topOffset = 100f
//                    leftOffset = 100f
//                }
//
//                elem(button) {
//                    topOffset = 100f
//                    leftOffset = 300f
//                }
//            }
        }
    }

    scene.attachCallbacks(this)
}
