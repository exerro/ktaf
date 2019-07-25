import ktaf.core.application
import ktaf.graphics.DrawContext2D
import ktaf.ui.elements.UIText
import ktaf.ui.layout.FillLayout
import ktaf.ui.node.UIContainer
import ktaf.ui.scene.attachCallbacks
import ktaf.ui.scene.scene

fun main() = application("Text input demo") {
    val context = DrawContext2D(screen)
    val scene = scene(display, context) {
        val root = root(UIContainer()) {
            layout(FillLayout())

            val input = children.add(UIText()) {
                width(300f)
                height(100f)
                text("Hello world! This will be a text box.")
            }
        }
    }

    scene.attachCallbacks(this)
}
