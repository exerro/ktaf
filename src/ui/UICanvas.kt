package ui

import AABB
import DrawContext2D
import plus
import push
import vec2

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
