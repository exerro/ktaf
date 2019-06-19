package ui

import DrawContext2D
import RGB
import rectangle
import vec2

class UIContainer: UINode() {
    var background by property(RGB(1f))

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.colour = background
        context.rectangle(position, size)
        super.draw(context, position, size)
    }
}
