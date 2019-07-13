package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.*
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.graphics.TextForeground
import ktaf.ui.layout.Border
import ktaf.ui.node.*
import ktaf.util.Animation
import lwjglkt.GLFWCursor

class UIButton(text: String): UINode() {
    private var background = addBackground(ColourBackground())
    private var foregroundText = addForeground(TextForeground(text))

    override val cursor: GLFWCursor? = GLFWCursor.POINTER

    var text = UIProperty(foregroundText.text)
    var textColour = UIAnimatedProperty(foregroundText.colour, this, "textColour", duration = Animation.QUICK)
    var colour = UIAnimatedProperty(background.colour, this, "colour", duration = Animation.QUICK) {
        this[DEFAULT_STATE](it)
        this[HOVER](it.lighten())
        this[PRESSED](it.darken())
    }
    var font = UIProperty(foregroundText.font)
    val onClick = EventHandlerList<UIEvent>()

    init {
        propertyState(colour)
        propertyState(this.text)
        propertyState(textColour)
        propertyState(font)

        colour.connect { colour ->
            background = replaceBackground(background, background.copy(colour = colour))
        }

        this.text.connect { t ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = t))
        }

        textColour.connect { colour ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour))
        }

        font.connect { font ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font))
        }

        onKeyPress { onClick.trigger(it) }
        onMouseClick { onClick.trigger(it) }
        onMousePress { state.push(PRESSED) }
        onMouseRelease { state.remove(PRESSED) }

        onMouseEnter { state.push(HOVER) }
        onMouseExit { state.remove(HOVER) }

        colour(rgba(0.27f, 0.54f, 0.81f))
        padding(Border(8f, 16f))
    }

    companion object {
        const val PRESSED = "pressed"
        const val HOVER = "hover"
    }
}
