package ktaf.property

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

    fun apply(value: Float) = constant + relative * value

    override fun toString() = when {
        relative == 0f -> "$constant"
        constant == 0f -> "${relative * 100}%"
        else -> "($constant + ${relative * 100}%)"
    }
}

val Float.px get() = Ratio(this, 0f)
val Float.percent get() = Ratio(0f, this / 100)
