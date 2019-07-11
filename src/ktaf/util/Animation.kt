package ktaf.util

import ktaf.core.*
import ktaf.typeclass.minus
import ktaf.typeclass.plus
import ktaf.typeclass.times

typealias EasingFunction = (Float, Float) -> Float
typealias AnimationEvaluator<T> = (T, T, Float) -> T

object Easing {
    val LINEAR: EasingFunction = { clock, duration -> clock / duration }
    val SMOOTH: EasingFunction = { clock, duration -> (clock / duration) .let { t -> 3 * t * t - 2 * t * t * t } }
}

class Animation<T>(
        val initial: T,
        val final: T,
        val duration: Float,
        val easing: EasingFunction,
        val eval: AnimationEvaluator<T>,
        val set: (T) -> Unit
) {
    private var clock = 0f

    fun update(dt: Float) {
        clock += dt
        if (clock > duration) clock = duration
        set(eval(initial, final, easing(clock, duration)))
    }

    fun finished() = clock >= duration

    companion object {
        val Float: AnimationEvaluator<Float> = { a, b, t -> a + (b - a) * t }
        val vec2: AnimationEvaluator<vec2> = { a, b, t -> a + (b - a) * t }
        val vec3: AnimationEvaluator<vec3> = { a, b, t -> a + (b - a) * t }
        val vec4: AnimationEvaluator<vec4> = { a, b, t -> a + (b - a) * t }
        val Int: AnimationEvaluator<Int> = { a, b, t -> (a + (b - a) * t).toInt() }

        val QUICK = 0.15f
        val NORMAL = 0.3f
        val SLOW = 0.45f
    }
}
