import ktaf.core.application
import ktaf.ui.elements.UIButton
import ktaf.ui.layout.Border
import ktaf.ui.node.UIContainer
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene

fun main() = application {
    display("Display") {
        val scene = scene(this, context2D) {
            root(UIContainer()) {
                children.add(UIButton("Hello world")) {
                    margin(Border(top=50f, left=50f))
                    width(200f)
                    height(300f)

                    clicked.connect {
                        text("Clicked at ${(time * 10).toInt() / 10f}s")
                        width(Math.random().toFloat() * 200 + 200)
                    }
                }
            }
        }

        scene.attachCallbacks(this)
    }
}
