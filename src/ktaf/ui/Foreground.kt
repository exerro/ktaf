package ktaf.ui

import ktaf.util.AABB
import ktaf.core.*
import ktaf.graphics.Font
import ktaf.graphics.widthOf
import ktaf.graphics.*
import lwjglkt.GLTexture2
import ktaf.util.wrapText

abstract class Foreground {
    abstract fun draw(context: DrawContext2D, position: vec2, size: vec2)
    open fun getHeight(width: Float): Float? = null
}

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

    // TODO: override fun getHeight(width: Float): Float? = height of wrapped text
}

data class ImageForeground(
        val image: GLTexture2,
        val tint: RGB = rgb(1f),
        val opacity: Float = 1f
): Background() {
    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.push {
            context.scissor = AABB(position, position + size)
            context.colour = tint.rgba(opacity)

            context.draw {
                image(image, position, size / image.size)
            }
        }
    }

    // TODO: override fun getHeight(width: Float): Float? = height of scaled image
}
