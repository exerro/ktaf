package ktaf.ui.elements

import ktaf.core.rgba
import ktaf.ui.*
import ktaf.util.Animation
import lwjglkt.GLFWCursor

class UIButton(text: String): UINode() {
    private var background = addBackground(ColourBackground(rgba(0.3f, 0.6f, 0.9f)))
    private var foregroundText = addForeground(TextForeground(text))

    override val cursor: GLFWCursor? = GLFWCursor.POINTER

    var text = UIProperty(foregroundText.text)
    var textColour = UIAnimatedProperty(foregroundText.colour, this, "textColour", duration = Animation.QUICK)
    var colour = UIAnimatedProperty(background.colour, this, "colour", duration = Animation.QUICK)
    var font = UIProperty(foregroundText.font)

    init {
        state.connect(colour::setState)
        state.connect(this.text::setState)
        state.connect(textColour::setState)
        state.connect(font::setState)
        colour["hover"](rgba(0.4f, 0.7f, 1f))

        colour.connect { colour ->
            background = replaceBackground(background, background.copy(colour = colour))
        }

        this.text.connect { text ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = text))
        }

        textColour.connect { colour ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour))
        }

        font.connect { font ->
            foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font))
        }

        onMouseEnter { state.set("hover") }
        onMouseExit { state.clear() }
    }
}
