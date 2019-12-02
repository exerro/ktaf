package ktaf.data

import ktaf.data.property.AnimatedProperty

data class Ratio(
        val constant: Float,
        val relative: Float
) {
    operator fun plus(other: Ratio) = Ratio(
            constant + other.constant,
            relative + other.relative
    )

    operator fun minus(other: Ratio) = Ratio(
            constant - other.constant,
            relative - other.relative
    )

    operator fun times(factor: Float) = Ratio(
            constant * factor,
            relative * factor
    )

    fun apply(value: Float) = constant + relative * value

    override fun toString() = when {
        relative == 0f -> "$constant"
        constant == 0f -> "${relative * 100}%"
        else -> "($constant + ${relative * 100}%)"
    }
}

val Float.px get() = Ratio(this, 0f)
val Float.percent get() = Ratio(0f, this / 100)

val Int.px get() = toFloat().px
val Int.percent get() = toFloat().percent

fun ratioAnimatedProperty(initial: Ratio, fn: AnimatedProperty<Ratio>.() -> Unit = {})
        = AnimatedProperty(initial) { a, b, t -> a * (1 - t) + b * t }
