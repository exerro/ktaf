package ktaf.property

/** A function mapping the clock and duration to an animation parameter */
typealias EasingFunction = (Float) -> AnimationParameter

object EasingFunctions {
    val linear = easing { it }
    val smooth = easing { t -> 3 * t * t - 2 * t * t * t }
}

fun easing(fn: (Float) -> AnimationParameter): EasingFunction = fn
