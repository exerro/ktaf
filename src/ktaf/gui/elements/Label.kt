package ktaf.gui.elements

import geometry.vec2
import ktaf.data.property.mutableProperty
import ktaf.graphics.*
import ktaf.gui.core.*
import lwjglktx.font.Font
import lwjglktx.font.widthOf

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
    val background = Background(colour)
    val text = mutableProperty(text)
    val textColour = colourProperty(textColour)
    val textAlignment = alignment2DProperty(vec2(0.5f))
    val font = mutableProperty(null as Font?)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth(): Float
            = (font.value ?: drawContext.DEFAULT_FONT).widthOf(text.value) + padding.value.width

    override fun getDefaultHeight(width: Float): Float
            = (font.value ?: drawContext.DEFAULT_FONT).lineHeight + padding.value.height

    override fun draw() {
        val font = font.value ?: drawContext.DEFAULT_FONT
        val space = size - padding.value.size - vec2(font.widthOf(text.value), font.lineHeight)

        background.draw(drawContext, position, size)
        drawContext.colour.value = textColour.value
        drawContext.write(text.value, position + padding.value.topLeft + space * textAlignment.value, font)
    }

    ////////////////////////////////////////////////////////////////////////////

    init {
        addAnimatedProperty(background.colour)
        addAnimatedProperty(background.imageAlignment)
    }
}
