package ktaf.ui.elements

import ktaf.KTAFMutableValue
import lwjglkt.GLFWCursor
import ktaf.core.rgba
import ktaf.ui.*

class UIContainer: UINode() {
    private var background = addBackground(ColourBackground(rgba(1f, 0.1f)))

    override val cursor: GLFWCursor? = null

    var colour = KTAFMutableValue(background.colour)

    init {
        colour.connect { colour ->
            background = replaceBackground(background, background.copy(colour = colour))
        }
    }
}
