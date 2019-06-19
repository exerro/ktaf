import core.*
import graphics.DrawContext2D
import graphics.circle
import graphics.rectangle
import ui.*
import ui.elements.UIButton
import ui.elements.UICanvas
import ui.elements.onClick
import ui.elements.onDraw
import kotlin.math.min
import kotlin.math.sin

fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
    val scene = scene(context) {
        lateinit var r: UIContainer
        r = root(UIContainer()) {
            background = rgba(0f, 1f, 0.5f)

            val b1 = list {
                width = 200f
                background = rgba(1f, 0f, 0f)

                addChild(UIButton("Hello")) {
                    colour = rgba(0f, 1f, 0f)
                    textColour = rgba(1f, 0f, 1f)
                    font = font.scaleTo(font.height * 1.3f)
                    height = 50f

                    onClick {
                        r.layout(GridLayout()) {
                            horizontal = 5
                            vertical = 5
                        }
                    }
                }

                addChild(UIButton("Button")) {
                    width = 100f
                    height = 30f
                    textColour = rgba(0f)

                    onClick {
                        r.layout(FlowLayout()) {
                            horizontalSpacing = Spacing.SPACE_BETWEEN
                            verticalSpacing = Spacing.SPACE_AFTER
                        }
                    }
                }

//                layout(ListLayout()) {
//                    spacing = Spacing.fixed(20f) within Spacing.SPACE_BEFORE
//                }
            }

            val b2 = addChild(UIButton("Woah")) {
                width = 200f
                height = 30f
                textColour = rgba(0f)

                onClick {
                    r.layout(ListLayout()) {
                        alignment = 0.8f
                        spacing = Spacing.SPACE_AFTER
                    }
                }
            }

            val buttons = (3..17).map {
                addChild(UIButton("B${it-2}")) {
                    colour = rgba(it.toFloat() / 25)
                    margin = Border(10f)
                    width = 100f
                    height = 50f

                    onClick { event ->
                        println("grid button ${it - 2} was clicked at ${event.position} with button ${event.button} and modifiers ${event.modifiers}")
                    }
                }
            }

            addChild(UICanvas()) {
                var colour = rgba(1f, 0f, 1f)

                width = 100f
                height = 100f

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

                onMousePress { event ->
                    event.ifNotHandled {
                        if (event.within(this)) {
                            event.handledBy(this)
                        }
                    }
                }

                onMouseClick { event ->
                    event.ifNotHandled {
                        if (event.within(this)) {
                            event.handledBy(this)
                            width = Math.random().toFloat() * 100f + 50f
                            height = Math.random().toFloat() * 100f + 50f
                        }
                    }
                }
            }

            layout(FreeLayout()) {
                alignment = vec2(0.5f)

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
