package ktaf.ui.graphics

import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.graphics.rectangle

data class ColourBackground(
        val colour: RGBA = rgba(1f)
): Background() {
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.colour = colour
            rectangle(position, size)
        }
    }
}
