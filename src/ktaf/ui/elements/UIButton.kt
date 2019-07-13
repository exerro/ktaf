package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.*
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.graphics.TextForeground
import ktaf.ui.layout.Border
import ktaf.ui.node.*
import ktaf.ui.typeclass.Clickable
import ktaf.util.Animation
import lwjglkt.GLFWCursor

class UIButton(text: String): UINode(), Clickable {
    private var background = addBackground(ColourBackground())
    private var foregroundText = addForeground(TextForeground(text))

    val onClick = EventHandlerList<UIEvent>()
    var text = UIProperty(foregroundText.text)
    var textColour = UIAnimatedProperty(foregroundText.colour, this, "textColour", duration = Animation.QUICK)
    var font = UIProperty(foregroundText.font)
    var colour = UIAnimatedProperty(background.colour, this, "colour", duration = Animation.QUICK) {
        this[DEFAULT_STATE](it)
        this[HOVER](it.lighten())
        this[PRESSED](it.darken())
    }

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER

    override fun click(event: UIEvent) {
        onClick.trigger(event)
    }

    init {
        propertyState(colour)
        propertyState(this.text)
        propertyState(textColour)
        propertyState(font)

        colour.connect { colour -> background = replaceBackground(background, background.copy(colour = colour)) }
        this.text.connect { t -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = t)) }
        textColour.connect { colour -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour)) }
        font.connect { font -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font)) }

        onKeyPress { onClick.trigger(it) }
        onMouseClick { onClick.trigger(it) }
        onMousePress { state.push(PRESSED) }
        onMouseRelease { state.remove(PRESSED) }

        colour(Colour.blue)
        padding(Border(8f, 16f))
    }

    companion object {
        const val PRESSED = "pressed"
    }
}
