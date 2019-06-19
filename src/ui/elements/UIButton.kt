package ui.elements

import core.rgba
import core.vec2
import graphics.DrawContext2D
import graphics.rectangle
import ui.*

class UIButton(text: String): UINode() {
    internal val clickEventHandlers: EventHandlerList<UIMouseClickEvent> = mutableListOf()
    private var background = addBackground(ColourBackground(rgba(1f)))
    private var foregroundText = addForeground(TextForeground(text))

    var colour by property(background.colour)
    var text by property(foregroundText.text)
    var textColour by property(foregroundText.colour)
    var font by property(foregroundText.font)

    init {
        withProperty(::colour) {
            attachChangeToCallback { colour ->
                background = replaceBackground(background, background.copy(colour = colour))
            }
        }

        withProperty(::text) {
            attachChangeToCallback { text ->
                foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = text))
            }
        }

        withProperty(::textColour) {
            attachChangeToCallback { colour ->
                foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour))
            }
        }

        withProperty(::font) {
            attachChangeToCallback { font ->
                foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font))
            }
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

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.draw {
            context.colour = colour
            rectangle(position, size)
        }
        super.draw(context, position, size)
    }
}

fun UIButton.onClick(fn: UIButton.(UIMouseClickEvent) -> Unit) {
    clickEventHandlers.add { fn(this, it) }
}
