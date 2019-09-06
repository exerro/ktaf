package ktaf.ui.elements

import geometry.*
import ktaf.core.*
import ktaf.ui.*
import ktaf.ui.layout.Border
import ktaf.ui.node.*
import ktaf.ui.typeclass.Clickable
import lwjglkt.glfw.GLFWCursor
import observables.Signal

open class UIButton(text: String): UITextRenderer(), Clickable {
    val clicked = Signal<Event>()

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER

    override fun click(event: Event) {
        clicked.emit(event)
    }

    init {
        onKeyPress.connect { clicked.emit(it) }
        onMouseClick.connect { clicked.emit(it) }
        onMousePress.connect { state.push(PRESSED) }
        onMouseRelease.connect { state.remove(PRESSED) }

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
