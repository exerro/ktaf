package ktaf.ui.node

import ktaf.core.RGBA
import ktaf.core.vec2
import ktaf.graphics.*
import ktaf.util.wrapText
import kotlin.math.max

fun fillBackground(context: DrawContext2D, position: vec2, size: vec2,
        colour: RGBA
) {
    context.draw {
        context.colour = colour
        context.fill = true
        rectangle(position, size)
    }
}

fun drawText(context: DrawContext2D, position: vec2, size: vec2,
        text: String,
        font: Font,
        wrap: Boolean,
        alignment: vec2,
        colour: RGBA
) {
    val lines = if (wrap) wrapText(text, font, size.x) else listOf(text.replace("\n", " "))
    var y = position.y + (size.y - lines.size * font.height) * alignment.y

    context.draw {
        context.colour = colour
        lines.forEach { line ->
            val x = position.x + (size.x - font.widthOf(line)) * alignment.x
            write(line, font, vec2(x, y))
            y += font.height
        }
    }
}

fun textWidth(text: String, font: Font, wrap: Boolean): Float {
    return if (wrap) text.split("\n").map { font.widthOf(it) } .fold(0f, ::max) + 1
         else font.widthOf(text.replace("\n", " ")) + 1
}

fun textHeight(text: String, font: Font, wrap: Boolean, width: Float): Float {
    return if (wrap) wrapText(text, font, width).size * font.height else font.height
}
