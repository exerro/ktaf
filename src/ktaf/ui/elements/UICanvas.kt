package ktaf.ui.elements

import ktaf.graphics.DrawContext2D
import ktaf.graphics.push
import ktaf.core.vec2
import ktaf.typeclass.plus
import ktaf.ui.node.UINode
import ktaf.util.AABB

class UICanvas : UINode() {
    internal val onDrawCallbacks = mutableListOf<UICanvas.(DrawContext2D, vec2) -> Unit>()

    override fun draw(context: DrawContext2D, position: vec2, size: vec2) {
        super.draw(context, position, size)

        context.push {
            translate(position)
            scissor = AABB(position, position + size)
            onDrawCallbacks.forEach { it(context, size) }
        }
    }
}

fun UICanvas.onDraw(fn: UICanvas.(DrawContext2D, vec2) -> Unit) {
    onDrawCallbacks.add(fn)
}
