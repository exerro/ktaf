package ktaf.gui.elements

import ktaf.data.property.AnimatedProperty
import ktaf.graphics.Colour
import ktaf.graphics.DrawContext2D
import ktaf.graphics.RGBA
import ktaf.gui.core.GUIBuilderContext
import ktaf.gui.core.UIContainer
import ktaf.gui.core.UINode
import ktaf.gui.core.colourProperty

fun UIContainer<UINode>.panel(colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = addChild(Panel(colour)).also(fn)

fun GUIBuilderContext.panel(colour: RGBA = Colour.white, fn: Panel.() -> Unit = {})
        = Panel(colour).also(fn)

//////////////////////////////////////////////////////////////////////////////////////////

class Panel(colour: RGBA = Colour.white) : UINode() {
    val colour: AnimatedProperty<RGBA> = colourProperty(colour)

    ////////////////////////////////////////////////////////////////////////////

    override fun getDefaultWidth(): Float? = null
    override fun getDefaultHeight(width: Float): Float? = null

    override fun draw(context: DrawContext2D) {
        context.colour.value = colour.value
        context.rectangle(position, size)
    }
}
