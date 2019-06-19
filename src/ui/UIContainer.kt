package ui

import DrawContext2D
import RGB
import rectangle
import vec2

class UIContainer: UINode() {
    var background by property(RGB(1f))

    override fun draw(context: DrawContext2D, x: Float, y: Float, width: Float, height: Float) {
        context.colour = background
        context.rectangle(vec2(x, y), vec2(width, height))
        super.draw(context, x, y, width, height)
    }
}
