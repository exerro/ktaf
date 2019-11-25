package ktaf.gui.core

import observables.Signal
import kotlin.reflect.KProperty

class Property<T>(initial: T) {
    val changed = Signal<T>()
    val delegate by lazy { PropertyDelegate(this) }
    var value = initial
        private set

    fun setValue(value: T) {
        if (value != this.value) {
            this.value = value
            changed.emit(value)
        }
    }

    fun connect(fn: (T) -> Unit) {
        changed.connect(fn)
        fn(value)
    }

    inner class PropertyDelegate(private val property: Property<T>) {
        operator fun getValue(thisRef: Any?, prop: KProperty<*>)
                = property.value

        operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
            property.setValue(value)
        }
    }

    enum class State {
        ACTIVE,
        HOVER,
        PRESS
    }
}

class StyledProperty<T>(
        default: T,
        private val fn: (StyledProperty<T>, T) -> Unit = { _, _ -> }
) {
    private val values: MutableMap<Set<Property.State>, T> = mutableMapOf()
    private var current: T = default

    val changed = Signal<T>()
    val delegate by lazy { StyledPropertyDelegate(this) }

    init {
        values[setOf()] = default
    }

    internal fun setStates(states: Set<Property.State>) {
        val newValue = values[states] ?: values[setOf()]!!

        if (newValue != current) {
            current = newValue
            changed.emit(newValue)
        }
    }

    operator fun set(s0: Property.State, value: T) {
        values[setOf(s0)] = value
    }

    operator fun set(s0: Property.State, s1: Property.State, value: T) {
        values[setOf(s0, s1)] = value
    }

    operator fun set(s0: Property.State, s1: Property.State, s2: Property.State, value: T) {
        values[setOf(s0, s1, s2)] = value
    }

    inner class StyledPropertyDelegate(private val property: StyledProperty<T>) {
        operator fun getValue(thisRef: Any?, prop: KProperty<*>)
                = property.current

        operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
            fn(property, value)
        }
    }
}
