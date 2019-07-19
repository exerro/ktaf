package ktaf.core

data class RatioValue(val fixed: Float, val ratio: Float) {
    operator fun invoke(relativeTo: Float) = ratio * relativeTo + fixed
}

fun constant(value: Float) = RatioValue(value, 0f)
fun ratio(value: Float) = RatioValue(0f, value)
fun percentage(value: Float) = ratio(value / 100)
