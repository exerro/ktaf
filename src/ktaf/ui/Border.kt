package ktaf.ui

import ktaf.core.vec2

data class Border(
        val top: Float,
        val right: Float = top,
        val bottom: Float = top,
        val left: Float = right
)

val Border.tl
        get() = vec2(left, top)

val Border.size
        get() = vec2(width, height)

val Border.width
        get() = left + right

val Border.height
        get() = top + bottom
