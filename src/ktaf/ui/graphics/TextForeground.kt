package ktaf.ui.graphics

import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.graphics.Font
import ktaf.graphics.widthOf
import ktaf.graphics.write
import ktaf.util.wrapText
import kotlin.math.max

data class TextForeground(
        val text: String,
        val colour: RGBA = rgba(1f),
        val alignment: vec2 = vec2(0.5f),
        val font: Font = Font.DEFAULT_FONT.scaleTo(16f)
): Foreground() {
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        val lines = wrapText(text, font, size.x)
        var y = position.y + (size.y - lines.size * font.height) * alignment.y

        context.colour = colour
        context.draw {
            lines.forEach { line ->
                val x = position.x + (size.x - font.widthOf(line)) * alignment.x
                write(line, font, vec2(x, y))
                y += font.height
            }
        }
    }

    override fun computeWidth(): Float? {
        return text.split("\n").map { font.widthOf(it) + 1 } .fold(0f, ::max)
    }

    override fun computeHeight(width: Float): Float? {
        return wrapText(text, font, width).size * font.height
    }

    // TODO: override fun getHeight(width: Float): Float? = height of wrapped text
}
