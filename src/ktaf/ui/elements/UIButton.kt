package ktaf.ui.elements

import ktaf.KTAFMutableValue
import ktaf.core.rgba
import lwjglkt.GLFWCursor
import ktaf.ui.*

class UIButton(text: String): UINode() {
    internal val clickEventHandlers: EventHandlerList<UIMouseClickEvent> = mutableListOf()
    private var background = addBackground(ColourBackground(rgba(0.3f, 0.6f, 0.9f)))
    private var foregroundText = addForeground(TextForeground(text))

    override val cursor: GLFWCursor? = GLFWCursor.POINTER

    var colour = KTAFMutableValue(background.colour)
    var text = KTAFMutableValue(foregroundText.text)
    var textColour = KTAFMutableValue(foregroundText.colour)
    var font = KTAFMutableValue(foregroundText.font)

    init {
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

        onMousePress { event -> event.ifNotHandled {
            if (event.within(this)) {
                event.handledBy(this)
            }
        } }

        onMouseClick { event -> event.ifNotHandled {
            if (event.within(this)) {
                event.handledBy(this)
                clickEventHandlers.forEach { it(event) }
            }
        } }
    }
}

fun UIButton.onClick(fn: UIButton.(UIMouseClickEvent) -> Unit) {
    clickEventHandlers.add { fn(this, it) }
}
