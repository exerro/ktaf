package ktaf.ui

import ktaf.KTAFMutableValue
import ktaf.core.vec2
import ktaf.typeclass.Add
import ktaf.typeclass.Animateable
import ktaf.typeclass.Mul
import ktaf.typeclass.Sub

data class Border(
        val top: Float,
        val right: Float = top,
        val bottom: Float = top,
        val left: Float = right
): Add<Border, Border>, Sub<Border, Border>, Mul<Float, Border>, Animateable<Border> {
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

fun KTAFMutableValue<Border>.set(top: Float, right: Float = top, bottom: Float = top, left: Float = right) { set(Border(top, right, bottom, left)) }
