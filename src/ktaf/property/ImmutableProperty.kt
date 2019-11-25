package ktaf.property

fun <T> const(value: T) = ImmutableProperty(value)

class ImmutableProperty<T>(
        override val value: T
): Value<T> {
    override fun equals(other: Any?) = other is ImmutableProperty<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = "const(${value.toString()})"

    override val onChangeEvent: OnChangeEvent? = null
}
