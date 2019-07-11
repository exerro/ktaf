package ktaf.ui.graphics

import ktaf.core.*
import ktaf.graphics.DrawContext2D
import ktaf.graphics.image
import ktaf.graphics.push
import ktaf.typeclass.plus
import ktaf.util.AABB
import lwjglkt.GLTexture2

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
