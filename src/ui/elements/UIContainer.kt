package ui.elements

import GLFWCursor
import core.rgba
import ui.*

class UIContainer: UINode() {
    private var background = addBackground(ColourBackground(rgba(1f, 0.1f)))

    override val cursor: GLFWCursor? = null

    var colour by property(background.colour)

    init {
        p(::colour) {
            attachChangeToCallback { colour ->
                background = replaceBackground(background, background.copy(colour = colour))
            }
        }
    }
}
