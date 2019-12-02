package ktaf.data.property

import ktaf.data.OnChangeEvent
import ktaf.data.Value

fun <T> const(value: T) = ImmutableProperty(value)

class ImmutableProperty<T>(
        override val value: T
): Value<T> {
    override fun equals(other: Any?) = other is Value<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = "const(${value.toString()})"

    override val onChangeEvent: OnChangeEvent? = null
}
