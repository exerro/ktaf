package ktaf.ui.elements

import ktaf.core.*
import ktaf.ui.layout.Border
import ktaf.ui.typeclass.Clickable
import lwjglkt.GLFWCursor

class UILabel(text: String, target: Clickable? = null): UITextRenderer() {
    val target = KTAFValue(target)

    override fun cursor(): GLFWCursor? = GLFWCursor.POINTER.takeIf { target.get() != null }

    init {
        onKeyPress { this.target.get()?.click(it) }
        onMouseClick { this.target.get()?.click(it) }

        colour(rgba(1f, 0f))
        textColour(rgba(0f, 1f))
        text(text)
        alignment(vec2(0f, 0.5f))
        padding(Border(8f, 16f))
    }
}
