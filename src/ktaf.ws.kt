import geometry.vec2
import geometry.vec2_zero
import ktaf.core.application
import ktaf.data.observableListOf
import ktaf.data.percent
import ktaf.data.property.const
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.gui.core.Padding
import ktaf.gui.core.Spacing
import ktaf.gui.core.UINode
import ktaf.gui.core.scene
import ktaf.gui.elements.*
import lwjglkt.gl.bindIn
import lwjglkt.util.loadTextureFile2D
import lwjglkt.util.createFramebuffer

fun main() = application {
    window("Hello world", 1080, 720) { window ->
        val drawctx = DrawContext2D(window.glfwWindow.glContext, const(vec2(100f)))
        val current = window.glfwWindow.glContext.makeCurrent()
        val texture = current.loadTextureFile2D("C:\\Users\\bened\\Pictures\\3e4.jpg")
        val (fbo, tex) = current.createFramebuffer(100, 100)

        current.free()

        drawctx.begin()
        fbo.bindIn {
            drawctx.currentContext.gl.clearColour(1f, 0f, 0f)
            drawctx.currentContext.gl.clear()
            drawctx.colour.value = Colour.blue
            drawctx.rectangle(vec2(25f), vec2(50f, 70f))
        }
        drawctx.end()

        val scene = scene<UINode>(window) {
            root = stack {
                panel(Colour.yellow)
                hdiv {
                    image(tex)
                    vdiv(40.percent) {
                        button("Click me", colour = Colour.orange) {
                            clicked.subscribe(this) {
                                text.value = "Hello world ${(Math.random() * 100).toInt()}"
                            }
                        }
                        stack {
                            panel(Colour.white)
                            hdiv {
                                vdiv {
                                    panel(Colour.green)
                                    panel(Colour.blue)
                                    panel(Colour.purple)
                                }
                                vdiv {
                                    panel(Colour.yellow)
                                    panel(Colour.red)
                                    panel(Colour.orange)
                                }

                                padding.value = Padding(16f)
                            }
                        }
                    }
                    imageButton(texture) {
                        stretch.value = false
                        alignment.value = vec2_zero
                    }

                    val items = observableListOf(1, 2, 3, 4, 5)
                    var n = 6

                    vdiv(90.percent) {
                        list(items) { n ->
                            button("Button $n") {
                                font.value = window.drawContext2D.DEFAULT_FONT

                                clicked.subscribe(this) {
                                    items.remove(n)
                                }
                            }
                        }.apply {
                            spacing.value = Spacing.EVEN
                            padding.value = Padding(10f)
                        }

                        button("ADD A BUTTON") {
                            font.value = window.drawContext2D.DEFAULT_FONT.scaleTo(20f)

                            clicked.subscribe(this) {
                                items.add(n++)
                            }
                        }
                    }

                    spacing.value = 10f
                    padding.value = Padding(100f, 50f)
                }
            }
//            root = stack {
//                panel(Colour.white)
//
//                stack {
//                    padding.value = Padding(10f)
//
//                    image(tex)
//                }
//            }
        }

        scene.attach()

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
