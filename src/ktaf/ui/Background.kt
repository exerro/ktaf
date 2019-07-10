package ktaf.ui

import ktaf.core.*
import ktaf.graphics.*
import ktaf.typeclass.plus
import lwjglkt.GLTexture2
import ktaf.util.AABB

abstract class Background {
    abstract fun draw(context: DrawContext2D, position: vec2, size: vec2)
}

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

data class ImageBackground(
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
}
