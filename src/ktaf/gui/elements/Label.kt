package ktaf.gui.elements

import geometry.minus
import geometry.plus
import geometry.times
import geometry.vec2
import ktaf.data.property.mutableProperty
import ktaf.graphics.*
import ktaf.gui.core.*

fun UIContainer<UINode>.label(text: String, textColour: RGBA = Colour.black, colour: RGBA = Colour.white.alpha(0f), fn: Label.() -> Unit = {})
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

    // TODO: this won't work if it's using the default font :/
    override fun getDefaultWidth() = font.value?.widthOf(text.value)
    override fun getDefaultHeight(width: Float) = font.value?.height

    override fun draw(context: DrawContext2D) {
        val font = font.value ?: context.DEFAULT_FONT
        val space = size - padding.value.size - vec2(font.widthOf(text.value), font.height)
        context.colour.value = colour.value
        context.rectangle(position, size)
        context.colour.value = textColour.value
        context.write(text.value, position + padding.value.topLeft + space * alignment.value)
    }
}
