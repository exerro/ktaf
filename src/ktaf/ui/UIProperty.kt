package ktaf.ui

import ktaf.core.vec2
import ktaf.core.vec3
import ktaf.core.vec4
import ktaf.util.Animation
import ktaf.util.Easing
import ktaf.util.EasingFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

internal interface UI_t

class UIProperty<out N, T>(private val self: N, private var valueInternal: T) {
    private val onChangeCallbacks = mutableListOf<N.(T, T) -> Any?>()
    var value
        get() = valueInternal
        set(value) {
            if (valueInternal != value) {
                val oldValue = valueInternal
                valueInternal = value
                onChangeCallbacks.forEach { it(this.self, oldValue, value) }
            }
        }

    operator fun setValue(self: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

    operator fun getValue(self: Any, property: KProperty<*>): T {
        return valueInternal
    }

    fun <R> attachChangeCallback(fn: N.(T, T) -> R) {
        onChangeCallbacks.add(fn)
    }

    fun <R> detachChangeCallback(fn: N.(T, T) -> R) {
        onChangeCallbacks.remove(fn)
    }
}

fun <N, T, R> UIProperty<N, T>.attachChangeToCallback(fn: N.(T) -> R): N.(T, T) -> Unit {
    val cb: N.(T, T) -> Unit = { _, new -> fn(new) }
    attachChangeCallback(cb)
    return cb
}

inline fun <reified N: UI_t, reified T> N.property(value: T)
        = UIProperty(this, value)

inline fun <reified N: UI_t, reified T, R> N.p(property: KProperty0<T>, fn: UIProperty<N, T>.() -> R) {
    val p = this.property(property)

    if (p == null) {
        error("No property '${property.name}' of type ${T::class.simpleName} in class ${N::class.simpleName}")
    }
    else {
        fn(p)
    }
}

inline fun <reified N: UI_t, reified T> N.property(property: KProperty0<T>): UIProperty<N, T>? {
    property.isAccessible = true
    val delegate = property.getDelegate()

    return when {
        delegate != null && delegate::class.isSubclassOf(UIProperty::class) ->
            @Suppress("UNCHECKED_CAST") // safe unless the property was incorrectly initialised
            delegate as UIProperty<N, T>
        else -> null
    }
}

inline fun <reified N: UINode> N.animateNullable(
        property: KProperty0<Float?>,
        to: Float,
        duration: Float = Animation.NORMAL,
        noinline easing: EasingFunction = Easing.LINEAR
) {
    p(property) {
        value ?.let { notNullValue -> scene?.animateNullable(this@animateNullable, this, Animation(
                notNullValue,
                to,
                duration,
                easing,
                Animation.Float,
                { value = it }
        )) } ?: ({ value = to })()
    }
}

inline fun <reified N: UINode> N.animate(
        property: KProperty0<Float>,
        to: Float,
        duration: Float = Animation.NORMAL,
        noinline easing: EasingFunction = Easing.LINEAR
) {
    p(property) {
        scene?.animate(this@animate, this, Animation(
                value,
                to,
                duration,
                easing,
                Animation.Float,
                { value = it }
        ))
    }
}

inline fun <reified N: UINode> N.animate(
        property: KProperty0<vec2>,
        to: vec2,
        duration: Float = Animation.NORMAL,
        noinline easing: EasingFunction = Easing.LINEAR
) {
    p(property) {
        scene?.animate(this@animate, this, Animation(
                value,
                to,
                duration,
                easing,
                Animation.vec2,
                { value = it }
        ))
    }
}

inline fun <reified N: UINode> N.animate(
        property: KProperty0<vec3>,
        to: vec3,
        duration: Float = Animation.NORMAL,
        noinline easing: EasingFunction = Easing.LINEAR
) {
    p(property) {
        scene?.animate(this@animate, this, Animation(
                value,
                to,
                duration,
                easing,
                Animation.vec3,
                { value = it }
        ))
    }
}

inline fun <reified N: UINode> N.animate(
        property: KProperty0<vec4>,
        to: vec4,
        duration: Float = Animation.NORMAL,
        noinline easing: EasingFunction = Easing.LINEAR
) {
    p(property) {
        scene?.animate(this@animate, this, Animation(
                value,
                to,
                duration,
                easing,
                Animation.vec4,
                { value = it }
        ))
    }
}

inline fun <reified N: UINode, reified T> N.cancelAnimation(property: KProperty0<T>) {
    p(property) {
        scene?.cancelAnimation(this@cancelAnimation, this)
    }
}
