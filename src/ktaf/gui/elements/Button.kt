package ktaf.gui.elements

import geometry.vec2
import ktaf.data.property.mutableProperty
import ktaf.graphics.*
import ktaf.gui.core.*
import lwjglkt.glfw.*
import observables.Subscribable

fun UIContainer<UINode>.button(text: String, colour: RGBA = Colour.blue, textColour: RGBA = Colour.white, fn: Button.() -> Unit = {})
        = addChild(Button(text, colour, textColour)).also(fn)

fun GUIBuilderContext.button(text: String, colour: RGBA = Colour.blue, textColour: RGBA = Colour.white, fn: Button.() -> Unit = {})
        = Button(text, colour, textColour).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class Button(
        text: String,
        colour: RGBA = Colour.blue,
        textColour: RGBA = Colour.white
): UINode() {
    val text = mutableProperty(text)
    val colour = colourProperty(colour)
    val textColour = colourProperty(textColour)
    val alignment = alignment2DProperty(vec2(0.5f))
    val font = mutableProperty(null as Font?)
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

    override fun getDefaultWidth(): Float
            = (font.value ?: fallbackFont).widthOf(text.value) + padding.value.width

    override fun getDefaultHeight(width: Float): Float
            = (font.value ?: fallbackFont).height + padding.value.height

    override fun initialise(drawContext: DrawContext2D) {
        fallbackFont = drawContext.DEFAULT_FONT
    }

    override fun draw(context: DrawContext2D) {
        val font = font.value ?: fallbackFont
        val space = size - padding.value.size - vec2(font.widthOf(text.value), font.height)
        val colour = colour.value
                .let { if (hovering && !pressed) it.darken() else it }
                .let { if (pressed) it.lighten() else it }

        context.colour.value = colour
        context.rectangle(position, size)
        context.colour.value = textColour.value
        context.write(text.value, position + padding.value.topLeft + space * alignment.value)
    }

    ////////////////////////////////////////////////////////////////////////////

    private lateinit var fallbackFont: Font
    private var hovering = false
    private var pressed = false

    init {
        padding.value = Padding(16f, 32f)
        cursor.value = GLFWCursor.POINTER
    }
}
