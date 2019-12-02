package ktaf.ui.layout

import ktaf.typeclass.Animateable

data class LayoutValue internal constructor(val pixels: Float, val percentage: Float): Animateable<LayoutValue> {
    override fun add(v: LayoutValue): LayoutValue = LayoutValue(pixels + v.pixels, percentage + v.percentage)
    override fun sub(v: LayoutValue): LayoutValue = LayoutValue(pixels - v.pixels, percentage - v.percentage)
    override fun mul(v: Float): LayoutValue = LayoutValue(pixels * v, percentage * v)

    fun apply(size: Float) = pixels + percentage * size / 100
}

fun Float.px() = LayoutValue(this, 0f)
fun Float.pc() = LayoutValue(0f, this)
fun Int.px() = toFloat().px()
fun Int.pc() = toFloat().pc()
