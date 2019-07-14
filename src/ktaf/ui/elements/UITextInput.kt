package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.layout.Border
import ktaf.ui.node.push
import ktaf.ui.node.remove
import lwjglkt.GLFWCursor
import org.lwjgl.glfw.GLFW

// TODO: this needs a hell of a lot of work!
class UITextInput: UITextRenderer() {
    override fun handlesKey(key: GLFWKey, modifiers: Set<GLFWKeyModifier>): Boolean
            = super.handlesKey(key, modifiers) || focused.get()

    override fun handlesInput(): Boolean = focused.get()
    override fun cursor(): GLFWCursor? = GLFWCursor.IBEAM

    init {
        focused.connect { if (it) state.push(EDITING) else state.remove(EDITING) }

        onTextInput {
            text(text.get() + it.input)
        }

        onKeyPress { when (it.key) {
            GLFW.GLFW_KEY_BACKSPACE -> { if (text.get().isNotEmpty())
                text(text.get().substring(0, text.get().length - 1))
            }
        } }

        colour.setSetter {
            this[DEFAULT_STATE](it)
            this[HOVER](it)
            this[EDITING](it.lighten())
        }

        colour(rgba(0.75f))
        textColour(rgba(0f))
        alignment(vec2(0f, 0.5f))
        padding(Border(8f, 16f))
    }

    companion object {
        const val EDITING = "editing"
    }
}