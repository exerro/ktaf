package ktaf.ui.node

import geometry.plus
import geometry.vec2
import ktaf.graphics.DrawCtx
import ktaf.ui.layout.tl

fun UINode.drawChildren(children: List<UINode>, context: DrawCtx, position: vec2) {
    children.forEach {
        it.draw(context,
                position + padding.get().tl + it.computedPosition.get(),
                vec2(it.currentComputedWidth.get(), it.currentComputedHeight.get())
        )
    }
}

fun UIContainer.orderedChildren(): List<UINode>
        = ordering.get().apply(children)

fun UINode.previousChild()
        = parent.get() ?.let { it.children.getOrNull(it.children.indexOf(this) - 1) }

fun UINode.nextChild()
        = parent.get() ?.let { it.children.getOrNull(it.children.indexOf(this) + 1) }
