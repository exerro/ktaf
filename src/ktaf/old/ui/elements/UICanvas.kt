package ktaf.ui.elements

import geometry.vec2
import ktaf.graphics.DrawCtx
import ktaf.ui.node.UINode
import observables.BiSignal

open class UICanvas: UINode() {
    override fun computeContentWidth(width: Float?): Float = 0f
    override fun computeContentHeight(width: Float, height: Float?): Float = 0f

    val onDraw = BiSignal<DrawCtx, vec2>()

    override fun draw(context: DrawCtx, position: vec2, size: vec2) {
        context.push {
            context.translate(position)
            // TODO: implement scissors
            // scissor = AABB(position, position + size)
            onDraw.emit(context, size)
        }
    }
}
