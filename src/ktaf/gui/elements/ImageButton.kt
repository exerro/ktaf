package ktaf.gui.elements

import geometry.*
import ktaf.util.size
import ktaf.data.property.mutableProperty
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.graphics.RGBA
import ktaf.graphics.darken
import ktaf.gui.core.*
import lwjglkt.gl.GLTexture2
import lwjglkt.glfw.*
import observables.Subscribable
import kotlin.math.min

fun UIContainer.imageButton(image: GLTexture2, colour: RGBA = Colour.white, fn: ImageButton.() -> Unit = {})
        = addChild(ImageButton(image, colour)).also(fn)

fun GUIBuilderContext.imageButton(image: GLTexture2, colour: RGBA = Colour.white, fn: ImageButton.() -> Unit = {})
        = ImageButton(image, colour).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

// TODO: animated offset/scale so toggling stretch animates the image position/size
class ImageButton(
        image: GLTexture2,
        colour: RGBA = Colour.white
): UINode() {
    val image = mutableProperty(image)
    val colour = colourProperty(colour)
    val alignment = animatedAlignment2DProperty(vec2(0.5f))
    val stretch = mutableProperty(true)
    val clicked = Subscribable<MouseClickEvent>()

    ////////////////////////////////////////////////////////////////////////////

    override fun entered() {
        hovering = true
    }

    override fun exited() {
        hovering = false
    }

    override fun handleMouseEvent(event: MouseEvent) { when (event) {
        is MousePressEvent -> pressed = true
        is MouseReleaseEvent -> pressed = false
        is MouseClickEvent -> clicked.emit(event)
    } }

    override fun getDefaultWidth() = image.value.width.toFloat()
    override fun getDefaultHeight(width: Float) = image.value.height.toFloat() * width / image.value.width.toFloat()

    override fun draw() {
        val scale = if (stretch.value)
            vec2(size.x / image.value.width, size.y / image.value.height)
        else
            vec2(min(size.x / image.value.width, size.y / image.value.height))
        val offset = (size - image.value.size * scale) * alignment.value

        drawContext.colour.value = colour.value
                .let { if (hovering && !pressed) it.darken() else it }
        drawContext.image(image.value, position + offset, scale)
    }

    ////////////////////////////////////////////////////////////////////////////

    private var hovering = false
    private var pressed = false

    init {
        expand.value = true
        cursor.value = GLFWCursor.POINTER
    }
}
