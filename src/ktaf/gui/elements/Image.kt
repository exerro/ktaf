package ktaf.gui.elements

import geometry.*
import ktaf.util.size
import ktaf.data.property.mutableProperty
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.graphics.RGBA
import ktaf.gui.core.*
import lwjglkt.gl.GLTexture2
import kotlin.math.min

fun UIContainer<UINode>.image(image: GLTexture2, colour: RGBA = Colour.white, fn: Image.() -> Unit = {})
        = addChild(Image(image, colour)).also(fn)

fun GUIBuilderContext.image(image: GLTexture2, colour: RGBA = Colour.white, fn: Image.() -> Unit = {})
        = Image(image, colour).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

// TODO: animated offset/scale so toggling stretch animates the image position/size
class Image(
        image: GLTexture2,
        colour: RGBA = Colour.white
): UINode() {
    val image = mutableProperty(image)
    val colour = colourProperty(colour)
    val alignment = animatedAlignment2DProperty(vec2(0.5f))
    val stretch = mutableProperty(true)

    override fun getDefaultWidth() = image.value.width.toFloat()
    override fun getDefaultHeight(width: Float) = image.value.height.toFloat() * width / image.value.width.toFloat()

    override fun draw(context: DrawContext2D) {
        val scale = if (stretch.value)
            vec2(size.x / image.value.width, size.y / image.value.height)
        else
            vec2(min(size.x / image.value.width, size.y / image.value.height))
        val offset = (size - image.value.size * scale) * alignment.value

        context.colour.value = colour.value
        context.image(image.value, position + offset, scale)
    }

    init {
        expand()
    }
}
