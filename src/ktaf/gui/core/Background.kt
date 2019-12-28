package ktaf.gui.core

import geometry.vec2
import ktaf.data.property.mutableProperty
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.graphics.RGBA
import ktaf.util.size
import lwjglkt.gl.GLTexture2
import kotlin.math.min

class Background(colour: RGBA = Colour.white) {
    val colour = colourProperty(colour)
    val image = mutableProperty(null as GLTexture2?)
    val imageAlignment = animatedAlignment2DProperty(vec2(0.5f))
    val stretchImage = mutableProperty(true)

    fun draw(
            context: DrawContext2D,
            position: vec2,
            size: vec2,
            colourTransform: (RGBA) -> RGBA = { it }
    ) {
        val image = image.value

        context.colour.value = colourTransform(colour.value)

        if (image != null) {
            val scale = if (stretchImage.value)
                vec2(size.x / image.width, size.y / image.height)
            else
                vec2(min(size.x / image.width, size.y / image.height))
            val offset = (size - image.size * scale) * imageAlignment.value

            context.image(image, position + offset, scale)
        }
        else {
            context.rectangle(position, size)
        }
    }
}
