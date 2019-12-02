package ktaf.ui.elements

import geometry.vec2
import ktaf.core.Colour
import ktaf.core.Event
import ktaf.core.darken
import ktaf.core.lighten
import ktaf.ui.DEFAULT_STATE
import ktaf.ui.layout.Border
import ktaf.ui.node.push
import ktaf.ui.node.remove
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
