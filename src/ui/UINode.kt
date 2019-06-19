package ui

import DrawContext2D
import RGB
import vec2
import rectangle
import application
import draw
import update

abstract class UINode: UI_t {
    internal var computedX: Float = 0f
    internal var computedY: Float = 0f
    internal var computedWidth: Float = 0f
    internal var computedHeight: Float = 0f
    internal val children = mutableListOf<UINode>()
    var margin by property(Border(0f))
    var padding by property(Border(0f))
    var layout by property(ListLayout() as UILayout)
    var width by property(null as Float?)
    var height by property(null as Float?)

    open fun computeHeight(width: Float) = height

    open fun update(dt: Float) {}
    open fun draw(context: DrawContext2D, x: Float, y: Float, width: Float, height: Float) {
        children.forEach {
            it.draw(context, x + it.computedX + it.margin.left, y + it.computedY + it.margin.top, it.computedWidth, it.computedHeight)
        }
    }
}

fun <N: UINode, C: UINode> N.addChild(child: C, init: C.() -> Unit = {}): C {
    children.add(child)
    init(child)
    return child
}

fun <N: UINode, C: UINode> N.removeChild(child: C): C {
    children.remove(child)
    return child
}

class UIButton: UINode() {
    var colour by property(RGB(1f))

    override fun draw(context: DrawContext2D, x: Float, y: Float, width: Float, height: Float) {
        context.colour = colour
        context.rectangle(vec2(x, y), vec2(width, height))
        super.draw(context, x, y, width, height)
    }
}

fun main() = application("Hello world") {
    val context = DrawContext2D(viewport)
    val scene = scene(context) {
        root(UIContainer()) {
            background = RGB(0f, 1f, 0.5f)

            val b1 = list {
                background = RGB(1f, 0f, 0f)

                addChild(UIButton()) {
                    colour = RGB(0f, 1f, 0f)
                    height = 50f
                }

                addChild(UIButton()) {
                    width = 100f
                    height = 30f
                }

                layout(ListLayout()) {
                    spacing = Spacing.fixed(20f) within Spacing.SPACE_BEFORE
                }
            }

            val b2 = addChild(UIButton()) {
                width = 200f
                height = 30f
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
            }

            layout(GridLayout()) {
                horizontal = 5
                vertical = 5
            }

            (3 .. 25).forEach { addChild(UIButton()) {
                colour = RGB(it.toFloat() / 25)
                margin = Border(10f)
            } }
        }
    }

    update { dt ->
        scene.update(dt)
    }

    draw {
        scene.draw()
    }
}
