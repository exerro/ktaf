package ktaf.ui.elements

import ktaf.core.rgba
import ktaf.ui.*
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.node.UINode
import ktaf.ui.node.addBackground
import ktaf.ui.node.replaceBackground
import lwjglkt.GLFWCursor

class UIContainer: UINode() {
    private var background = addBackground(ColourBackground(rgba(1f, 0.1f)))

    override val cursor: GLFWCursor? = null

    var colour = UIAnimatedProperty(background.colour, this, "colour")

    init {
        state.connect(colour::setState)

        colour.connect { colour ->
            background = replaceBackground(background, background.copy(colour = colour))
        }
    }
}