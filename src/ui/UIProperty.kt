package ui

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

internal interface UI_t

class UIProperty<out N, T>(private val self: N, private var value: T) {
    private val onChangeCallbacks = mutableListOf<N.(T, T) -> Unit>()

    operator fun setValue(self: Any, property: KProperty<*>, value: T) {
        if (this.value != value) {
            val oldValue = this.value
            this.value = value
            onChangeCallbacks.forEach { it(this.self, oldValue, value) }
        }
    }

    operator fun getValue(self: Any, property: KProperty<*>): T {
        return value
    }

    fun attachChangeCallback(fn: N.(T, T) -> Unit) {
        onChangeCallbacks.add(fn)
    }

    fun detachChangeCallback(fn: N.(T, T) -> Unit) {
        onChangeCallbacks.remove(fn)
    }
}

fun <N, T> UIProperty<N, T>.attachChangeToCallback(fn: N.(T) -> Unit): N.(T, T) -> Unit {
    val cb: N.(T, T) -> Unit = { _, new -> fn(new) }
    attachChangeCallback(cb)
    return cb
}

internal inline fun <reified N: UI_t, reified T> N.property(value: T)
        = UIProperty(this, value)

inline fun <reified N: UI_t, reified T> N.withProperty(property: KProperty0<T>, fn: UIProperty<N, T>.() -> Unit) {
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
