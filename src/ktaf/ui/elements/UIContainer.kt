package ktaf.ui.elements

import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.ui.*
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.node.UINode
import ktaf.ui.node.addBackground
import ktaf.ui.node.replaceBackground
import lwjglkt.GLFWCursor

class UIContainer: UINode() {
    private var background = addBackground(ColourBackground(rgba(1f, 0.02f)))

    override val cursor: GLFWCursor? = null

    var colour = UIAnimatedProperty(background.colour, this, "colour")

    init {
        propertyState(colour)

        colour.connect { colour ->
            background = replaceBackground(background, background.copy(colour = colour))
        }
    }
}
