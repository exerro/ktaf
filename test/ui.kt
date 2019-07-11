
import ktaf.core.invoke
import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.circle
import ktaf.graphics.rectangle
import ktaf.ui.*
import ktaf.ui.elements.*
import ktaf.ui.layout.*
import kotlin.math.min
import kotlin.math.sin

fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
    val scene = scene(display, context) {
        lateinit var r: UIContainer

        r = setRoot(UIContainer()) {
            colour(rgba(0f, 1f, 0.5f))


            val b1 = list {
                colour(rgba(1f, 0f, 0f))

                shrink()

                addChild(UIButton("Hello")) {
                    colour(rgba(0.3f, 0.9f, 0.6f))
                    width["hover"](100f)
                    margin["hover"](Border(0f, 32f))
                    textColour(rgba(1f, 0f, 1f))
                    font(font.get().scaleTo(font.get().height * 1.3f))
                    height(50f)

                    fill()

                    onClick {
                        r.layout(GridLayout()) {
                            columns(5)
                            rows(5)
                        }
                    }
                }

                addChild(UIButton("Button")) {
                    width(120f)
                    height(30f)
                    textColour(rgba(0f))

                    onClick {
                        r.layout(FlowLayout()) {
                            horizontalSpacing(Spacing.SPACE_BETWEEN)
                            verticalSpacing(Spacing.SPACE_AFTER)
                        }
                    }
                }

//                layout(ListLayout()) {
//                    spacing = Spacing.fixed(20f) within Spacing.SPACE_BEFORE
//                }
            }

            val b2 = addChild(UIButton("Woah")) {
                fill()
                height(30f)
                textColour(rgba(0f))

                onClick {
                    r.layout(ListLayout()) {
                        alignment(0.8f)
                        spacing(Spacing.SPACE_AFTER)
                    }
                }
            }

            val buttons = (3..17).map {
                addChild(UIButton("B${it - 2}")) {
                    colour(rgba(it.toFloat() / 25))
                    margin(Border(10f))
                    width(100f)
                    height(50f)

                    onClick { event ->
                        println("grid button ${it - 2} was clicked at ${event.position} with button ${event.button} and modifiers ${event.modifiers}")
                        height((Math.random() * 100).toFloat() + 50f)
                    }
                }
            }

            addChild(UICanvas()) {
                var colour = rgba(1f, 0f, 1f)

                width(100f)
                height(100f)

                onDraw { context, size ->
                    context.draw {
                        context.colour = rgba(0f, 0f, 1f)
                        rectangle(vec2(0f), size)
                        context.colour = colour
                        circle(size / 2f, min(size.x, size.y) / 2)
                    }
                }

                onMouseEnter {
                    println("Here")
                    colour = rgba(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())
                }

                onMouseClick {
                    width(Math.random().toFloat() * 100f + 50f)
                    height(Math.random().toFloat() * 100f + 50f)
                }
            }

            layout(FreeLayout()) {
                alignment(vec2(0.5f))

                hline("top") { percentage = 0f }
                hline("middle") { percentage = 80f }
                hline("bottom") { percentage = 100f }

                elem(b1) {
                    top = "top"
                    bottom = "middle"
                    leftPercentage = 50f
                    rightPercentage = 100f
                    rightOffset = -20f
                }

                elem(b2) {
                    top = "middle"
                    bottom = "bottom"
                    rightPercentage = 100f
                }

                buttons.forEachIndexed { i, it ->
                    elem(it) {
                        topOffset = sin(i.toFloat() / 3f) * 80f + 80f
                        leftOffset = i * 20f
                    }
                }
            }
        }
    }

    scene.attachCallbacks(this)
}
