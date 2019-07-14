package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.*
import ktaf.ui.layout.Border
import ktaf.ui.node.*
import ktaf.ui.typeclass.Clickable
import lwjglkt.GLFWCursor

class UIButton(text: String): UITextRenderer(), Clickable {
    val onClick = EventHandlerList<Event>()

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER

    override fun click(event: Event) {
        onClick.trigger(event)
    }

    init {
        onKeyPress { onClick.trigger(it) }
        onMouseClick { onClick.trigger(it) }
        onMousePress { state.push(PRESSED) }
        onMouseRelease { state.remove(PRESSED) }

        colour.setSetter {
            this[DEFAULT_STATE](it)
            this[HOVER](it.lighten())
            this[PRESSED](it.darken())
        }

        colour(Colour.blue)
        text(text)
        alignment(vec2(0.5f))
        padding(Border(8f, 16f))
    }

    companion object {
        const val PRESSED = "pressed"
    }
}
