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

class UILabel(text: String, target: Clickable? = null): UINode() {
    private var background = addBackground(ColourBackground())
    private var foregroundText = addForeground(TextForeground(text, alignment = vec2(0f, 0.5f), colour = rgba(0f)))

    val target = KTAFValue(target)
    var text = UIProperty(foregroundText.text)
    var textColour = UIAnimatedProperty(foregroundText.colour, this, "textColour", duration = Animation.QUICK)
    var font = UIProperty(foregroundText.font)
    var colour = UIAnimatedProperty(background.colour, this, "colour", duration = Animation.QUICK) {
        this[DEFAULT_STATE](it)
        this[HOVER](it.lighten())
    }

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER.takeIf { target.get() != null }

    init {
        propertyState(colour)
        propertyState(this.text)
        propertyState(textColour)
        propertyState(font)

        colour.connect { colour -> background = replaceBackground(background, background.copy(colour = colour)) }
        this.text.connect { t -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = t)) }
        textColour.connect { colour -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour)) }
        font.connect { font -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font)) }

        onKeyPress { this.target.get()?.click(it) }
        onMouseClick { this.target.get()?.click(it) }

        colour(rgba(1f, 0f))
        padding(Border(8f, 16f))
    }
}
