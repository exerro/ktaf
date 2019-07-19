package ktaf.ui.elements

import ktaf.core.KTAFList
import ktaf.graphics.DrawContext2D
import ktaf.core.vec2
import ktaf.typeclass.plus
import ktaf.ui.node.UINode
import ktaf.util.AABB

class UICanvas: UINode() {
    override fun computeContentWidth(width: Float?): Float = 0f
    override fun computeContentHeight(width: Float, height: Float?): Float = 0f

    val onDraw = KTAFList<UICanvas.(DrawContext2D, vec2) -> Unit>()

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        context.push {
            translate(position)
            scissor = AABB(position, position + size)
            onDraw.forEach { it(context, size) }
        }
    }
}
