package ktaf.gui.elements

import geometry.vec2
import ktaf.data.property.mutableProperty
import ktaf.graphics.*
import ktaf.gui.core.*

fun UIContainer.label(text: String, textColour: RGBA = Colour.black, colour: RGBA = Colour.white.alpha(0f), fn: Label.() -> Unit = {})
        = addChild(Label(text, textColour, colour)).also(fn)

fun GUIBuilderContext.label(text: String, textColour: RGBA = Colour.black, colour: RGBA = Colour.white.alpha(0f), fn: Label.() -> Unit = {})
        = Label(text, textColour, colour).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class Label(
        text: String,
        textColour: RGBA = Colour.black,
        colour: RGBA = Colour.white.alpha(0f)
): UINode() {
    val text = mutableProperty(text)
    val colour = colourProperty(colour)
    val textColour = colourProperty(textColour)
    val alignment = alignment2DProperty(vec2(0.5f))
    val font = mutableProperty(null as Font?)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth(): Float
            = (font.value ?: drawContext.DEFAULT_FONT).widthOf(text.value) + padding.value.width

    override fun getDefaultHeight(width: Float): Float
            = (font.value ?: drawContext.DEFAULT_FONT).height + padding.value.height

    override fun draw() {
        val font = font.value ?: drawContext.DEFAULT_FONT
        val space = size - padding.value.size - vec2(font.widthOf(text.value), font.height)

        drawContext.colour.value = colour.value
        drawContext.rectangle(position, size)
        drawContext.colour.value = textColour.value
        drawContext.write(text.value, position + padding.value.topLeft + space * alignment.value, font)
    }

    ////////////////////////////////////////////////////////////////////////////

    init {

    }
}
