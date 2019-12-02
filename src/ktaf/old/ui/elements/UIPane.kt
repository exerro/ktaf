package ktaf.ui.elements

import geometry.vec2
import ktaf.core.RGBA
import ktaf.core.rgba
import ktaf.graphics.DrawCtx
import ktaf.ui.UIProperty
import ktaf.ui.node.UINode
import ktaf.ui.node.fillBackground

open class UIPane(colour: RGBA = rgba(1f, 0f)): UINode() {
    var colour = UIProperty(colour)

    override fun computeContentWidth(width: Float?): Float = 0f
    override fun computeContentHeight(width: Float, height: Float?): Float = 0f

    override fun draw(context: DrawCtx, position: vec2, size: vec2) {
        fillBackground(context, position, size, colour.get())
    }

    init {
        propertyState(this.colour)
    }
}
