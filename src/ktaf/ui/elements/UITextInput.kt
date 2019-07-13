package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.UIProperty
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.graphics.TextForeground
import ktaf.ui.layout.Border
import ktaf.ui.node.*
import ktaf.util.Animation
import lwjglkt.GLFWCursor
import org.lwjgl.glfw.GLFW

// TODO: this needs a hell of a lot of work!
class UITextInput: UINode() {
    private var background = addBackground(ColourBackground())
    private var foregroundText = addForeground(TextForeground("", alignment = vec2(0f, 0.5f), colour = rgba(0f), wrap = false))

    var text = UIProperty(foregroundText.text)
    var textColour = UIAnimatedProperty(foregroundText.colour, this, "textColour", duration = Animation.QUICK)
    var font = UIProperty(foregroundText.font)
    var colour = UIAnimatedProperty(background.colour, this, "colour", duration = Animation.QUICK) {
        this[DEFAULT_STATE](it)
        this[HOVER](it)
        this[EDITING](it.lighten())
    }

    override fun handlesKey(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): Boolean
            = super.handlesKey(key, modifiers) || focussed.get()

    override fun handlesInput(): Boolean = focussed.get()
    override fun cursor(): GLFWCursor? = GLFWCursor.IBEAM

    init {
        propertyState(colour)
        propertyState(text)
        propertyState(textColour)
        propertyState(font)

        colour.connect { colour -> background = replaceBackground(background, background.copy(colour = colour)) }
        text.connect { t -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(text = t)) }
        textColour.connect { colour -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(colour = colour)) }
        font.connect { font -> foregroundText = replaceForeground(foregroundText, foregroundText.copy(font = font)) }
        focussed.connect { if (it) state.push(EDITING) else state.remove(EDITING) }

        onTextInput {
            text(text.get() + it.input)
        }

        onKeyPress { when (it.key) {
            GLFW.GLFW_KEY_BACKSPACE -> { if (text.get().isNotEmpty())
                text(text.get().substring(0, text.get().length - 1))
            }
        } }

        colour(rgba(0.75f))
        padding(Border(8f, 16f))
    }

    companion object {
        const val EDITING = "editing"
    }
}