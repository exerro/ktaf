import ktaf.core.application
import ktaf.graphics.Colour
import ktaf.gui.core.Padding
import ktaf.gui.core.UINode
import ktaf.gui.core.scene
import ktaf.gui.elements.*
import lwjglkt.util.loadTextureFile2D

fun main() = application {
    display("Hello world", 1080, 720) { window ->
        val texture = window.glfwWindow.glContext.makeCurrent {
            it.loadTextureFile2D("C:\\Users\\bened\\Pictures\\3e4.jpg")
        }

        val scene = scene<UINode>(window.drawContext2D) {
            root = stack {
                panel(Colour.blue)
                vdiv {
                    panel(Colour.red)
                    image(texture)

                    spacing.value = 200f
                    padding.value = Padding(100f, 50f)
                }
            }
        }

        scene.attach(window)

        window.events.framebufferResized.subscribe(scene) {
            draw()
            update(0.1f)
            window.glfwWindow.swapBuffers()
        }

////        val texture = window.glfwWindow.glContext.loadTextureFile2D("C:\\Users\\bened\\Pictures\\3e4.jpg")
////
////        window.draw.connect {
////            window.drawContext2D.begin()
////            window.drawContext2D.colour <- const(Colour.red)
////            window.drawContext2D.rectangle(vec2(100f), vec2(200f))
////            window.drawContext2D.colour <- const(Colour.blue.alpha(0.5f))
////            window.drawContext2D.triangle(vec2(200f), vec2(300f), vec2(250f, 350f))
////            window.drawContext2D.colour <- const(Colour.green)
////            window.drawContext2D.line(vec2(100f), vec2(300f))
//            window.drawContext2D.line(vec2(100f, 200f), vec2(300f, 400f), 5f)
//            window.drawContext2D.colour <- const(Colour.purple)
//            window.drawContext2D.point(vec2(200f, 150f))
//            window.drawContext2D.point(vec2(250f, 150f), 10f)
//            window.drawContext2D.colour <- const(Colour.orange)
//            window.drawContext2D.write("Hello world")
//            window.drawContext2D.colour <- const(Colour.white)
//            window.drawContext2D.image(texture, vec2(400f), vec2(100f) / texture.size)
//            window.drawContext2D.end()
//        }
    }
}
