package ktaf.ui.node

import ktaf.core.vec2
import ktaf.typeclass.plus
import ktaf.ui.layout.height
import ktaf.ui.layout.tl
import ktaf.ui.layout.width


fun UINode.fill() { fillSize = true }
fun UINode.shrink() { fillSize = false }

fun UINode.absolutePosition(): vec2
        = (parent.get()?.absolutePosition() ?: vec2(0f)) + (parent.get()?.padding?.get()?.tl ?: vec2(0f)) + vec2(computedX.get(), computedY.get())

fun UINode.computeInternalWidth(): Float?
        = backgroundsInternal.firstNotNull { it.computeWidth() }
        ?: (foregroundsInternal.firstNotNull { it.computeWidth() } ?.let { it + padding.get().width })

fun UINode.computeInternalHeight(width: Float): Float?
        = backgroundsInternal.firstNotNull { it.computeHeight(width) }
        ?: (foregroundsInternal.firstNotNull { it.computeHeight(width) } ?.let { it + padding.get().height })
