package ktaf.util

import geometry.*
import ktaf.core.KTAFValue
import ktaf.typeclass.*
import kotlin.reflect.KProperty0

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
    val onFinish = KTAFValue <(T) -> Unit> {}

    fun update(dt: Float) {
        clock += dt
        if (clock > duration) clock = duration
        set(eval(initial, final, easing(clock, duration)))
    }

    fun finished() = clock >= duration

    fun cancel() {
        onFinish.get()(eval(initial, final, easing(clock, duration)))
    }

    fun finish() {
        set(final)
        onFinish.get()(final)
    }

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

class Animations {
    internal val animations: MutableMap<Any, MutableMap<String, Animation<*>>> = mutableMapOf()
}

fun Animations.update(dt: Float) {
    for ((_, m) in animations) for ((_, animation) in m)
        animation.update(dt)

    animations.map { (_, m) -> m.values.filter { it.finished() } .forEach { it.finish() } }
    animations.map { (node, m) -> node to m.filterValues { !it.finished() } }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun <T: Animateable<T>> Animations.animate(owner: Any, property: KProperty0<KTAFValue<T>>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> Animations.animate(owner: Any, property: String, value: KTAFValue<T>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH): Animation<T> {
    return Animation(
            value.get(), to, duration, easing,
            { a, b, t -> a.transitionTo(b, t) }, value::setValue
    ).also { animations.computeIfAbsent(owner) { mutableMapOf() } [property] = it }
}

fun Animations.animate(owner: Any, property: KProperty0<KTAFValue<Float>>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun Animations.animate(owner: Any, property: String, value: KTAFValue<Float>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH): Animation<Float> {
    return Animation(
            value.get(), to, duration, easing,
            { a, b, t -> a + (b - a) * t }, value::setValue
    ).also { animations.computeIfAbsent(owner) { mutableMapOf() } [property] = it }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun <T: Animateable<T>> Animations.animateNullable(owner: Any, property: KProperty0<KTAFValue<T?>>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> Animations.animateNullable(owner: Any, property: String, value: KTAFValue<T?>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH): Animation<T>? {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return null
    }

    return Animation(
            prev, to, duration, easing,
            { a, b, t -> a.transitionTo(b, t) }, value::setValue
    ).also { animations.computeIfAbsent(owner) { mutableMapOf() } [property] = it }
}

fun Animations.animateNullable(owner: Any, property: KProperty0<KTAFValue<Float?>>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun Animations.animateNullable(owner: Any, property: String, value: KTAFValue<Float?>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH): Animation<Float>? {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return null
    }

    return Animation(
            prev, to, duration, easing,
            { a, b, t -> a + (b - a) * t }, value::setValue
    ).also { animations.computeIfAbsent(owner) { mutableMapOf() } [property] = it }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun Animations.cancelAnimation(owner: Any, property: String) {
    animations[owner] ?.let { it[property] ?.cancel() }
    animations.computeIfAbsent(owner) { mutableMapOf() } .remove(property)
}
