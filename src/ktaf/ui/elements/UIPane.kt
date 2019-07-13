package ktaf.ui.elements

import ktaf.core.rgba
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.graphics.ColourBackground
import ktaf.ui.node.UIContainer
import ktaf.ui.node.addBackground
import ktaf.ui.node.replaceBackground

class UIPane: UIContainer() {
    private var background = addBackground(ColourBackground(rgba(1f, 0.02f)))

    var colour = UIAnimatedProperty(background.colour, this, "colour")

    init {
        propertyState(colour)
        colour.connect { colour -> background = replaceBackground(background, background.copy(colour = colour)) }
    }
}
