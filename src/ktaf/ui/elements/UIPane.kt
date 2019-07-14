package ktaf.ui.elements

import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.node.UIContainer
import ktaf.ui.node.fillBackground

open class UIPane(colour: RGBA = rgba(1f, 0.02f)): UIContainer() {
    var colour = UIAnimatedProperty(colour, this, "colour")

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        fillBackground(context, position, size, colour.get())
        super.draw(context, position, size)
    }

    init {
        propertyState(this.colour)
    }
}
