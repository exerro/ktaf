package ktaf.ui.scene

import ktaf.core.KTAFMutableValue
import ktaf.typeclass.Animateable
import ktaf.typeclass.transitionTo
import ktaf.ui.node.UINode
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction
import kotlin.reflect.KProperty0

fun <T: Animateable<T>> UIScene.animate(owner: Any, property: KProperty0<KTAFMutableValue<T>>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> UIScene.animate(owner: Any, property: String, value: KTAFMutableValue<T>, to: T, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            value.get(),
            to,
            duration,
            easing,
            { a, b, t -> a.transitionTo(b, t) },
            value::setValue
    )
}

fun <T: Animateable<T>> UIScene.animateNullable(owner: Any, property: KProperty0<KTAFMutableValue<T?>>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun <T: Animateable<T>> UIScene.animateNullable(owner: Any, property: String, value: KTAFMutableValue<T?>, to: T?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return
    }

    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            prev,
            to,
            duration,
            easing,
            { a, b, t -> a.transitionTo(b, t) },
            value::setValue
    )
}

fun UIScene.animate(owner: Any, property: KProperty0<KTAFMutableValue<Float>>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animate(owner, property.name, property.get(), to, duration, easing)

fun UIScene.animate(owner: Any, property: String, value: KTAFMutableValue<Float>, to: Float, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            value.get(),
            to,
            duration,
            easing,
            { a, b, t -> a + (b - a) * t },
            value::setValue
    )
}

fun UIScene.animateNullable(owner: Any, property: KProperty0<KTAFMutableValue<Float?>>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH)
        = animateNullable(owner, property.name, property.get(), to, duration, easing)

fun UIScene.animateNullable(owner: Any, property: String, value: KTAFMutableValue<Float?>, to: Float?, duration: Float = Animation.NORMAL, easing: EasingFunction = Easing.SMOOTH) {
    val prev = value.get()

    if (prev == null || to == null) {
        value.setValue(to)
        return
    }

    animations.computeIfAbsent(owner) { mutableMapOf() } [property] = Animation(
            prev,
            to,
            duration,
            easing,
            { a, b, t -> a + (b - a) * t },
            value::setValue
    )
}

fun <N: UINode, T> UIScene.cancelAnimation(node: N, property: String) {
    animations.computeIfAbsent(node) { mutableMapOf() } .remove(property)
}
