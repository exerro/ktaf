package ktaf.gui.elements

import ktaf.graphics.Colour
import ktaf.graphics.RGBA
import ktaf.gui.core.Background
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import lwjglkt.gl.GLTexture2

fun UIContainer.panel(colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = addChild(Panel(colour)).also(fn)

fun GUIBuilderContext.panel(colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = Panel(colour).also(fn)

fun UIContainer.image(image: GLTexture2, colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = addChild(Panel(colour)).also(fn).also { it.background.image.value = image }

fun GUIBuilderContext.image(image: GLTexture2, colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = Panel(colour).also(fn).also { it.background.image.value = image }

//////////////////////////////////////////////////////////////////////////////////////////

class Panel(colour: RGBA = Colour.white) : UINode() {
    val background = Background(colour)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth(): Float? = null
    override fun getDefaultHeight(width: Float): Float? = null
    override fun draw() {
        background.draw(drawContext, position, size)
    }

    ////////////////////////////////////////////////////////////////////////////

    init {
        addAnimatedProperty(background.colour)
        addAnimatedProperty(background.imageAlignment)
    }
}
