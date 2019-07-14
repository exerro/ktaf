package ktaf.ui.elements

import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.core.vec2
import ktaf.graphics.DrawContext2D
import ktaf.ui.UIAnimatedProperty
import ktaf.ui.node.UINode
import ktaf.ui.node.fillBackground

open class UIPane(colour: RGBA = rgba(1f, 0f)): UINode() {
    var colour = UIAnimatedProperty(colour, this, "colour")

    override fun computeContentWidth(width: Float?): Float = 0f
    override fun computeContentHeight(width: Float, height: Float?): Float = 0f

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        fillBackground(context, position, size, colour.get())
    }

    init {
        propertyState(this.colour)
    }
}
