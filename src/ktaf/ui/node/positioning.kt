package ktaf.ui.node

import ktaf.core.vec2
import ktaf.typeclass.plus
import ktaf.ui.layout.tl


fun UINode.fill() { fillSize = true }
fun UINode.shrink() { fillSize = false }

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(computedX.get(), computedY.get())
