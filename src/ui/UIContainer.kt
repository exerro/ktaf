package ui

import core.rgba
import core.vec2
import graphics.DrawContext2D
import graphics.rectangle

class UIContainer: UINode() {
    var background by property(rgba(1f))

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.colour = background
            rectangle(position, size)
        }
        super.draw(context, position, size)
    }
}
