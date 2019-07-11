package ktaf.ui

data class RelativeSize(val fixed: Float, val ratio: Float) {
    operator fun invoke(relativeTo: Float) = ratio * relativeTo + fixed
}

fun fixed(value: Float) = RelativeSize(value, 0f)
fun ratio(value: Float) = RelativeSize(0f, value)
fun percentage(value: Float) = ratio(value / 100)
