package ktaf.ui.layout

import ktaf.core.vec2
import ktaf.typeclass.Add
import ktaf.typeclass.Animateable
import ktaf.typeclass.Mul
import ktaf.typeclass.Sub

data class Border(
        val top: Float = 0f,
        val right: Float = 0f,
        val bottom: Float = 0f,
        val left: Float = 0f
): Add<Border, Border>, Sub<Border, Border>, Mul<Float, Border>, Animateable<Border> {
    constructor(all: Float): this(all, all, all, all)
    constructor(vertical: Float, horizontal: Float): this(top = vertical, bottom = vertical, left = horizontal, right = horizontal)

    override fun add(v: Border): Border = Border(top + v.top, right + v.right, bottom + v.bottom, left + v.left)
    override fun sub(v: Border): Border = Border(top - v.top, right - v.right, bottom - v.bottom, left - v.left)
    override fun mul(v: Float): Border = Border(top * v, right * v, bottom * v, left * v)
}

val Border.tl
        get() = vec2(left, top)

val Border.size
        get() = vec2(width, height)

val Border.width
        get() = left + right

val Border.height
        get() = top + bottom
