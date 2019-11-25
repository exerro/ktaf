package ktaf.property

import ktaf.core.debug

fun <T> mutable(value: T) = MutableProperty(value)

//////////////////////////////////////////////////////////////////////////////////////////

open class MutableProperty<T>(
        protected var rawValue: T
): Value<T> {
    override var value
        get() = rawValue
        set(new) {
            disconnectBinding()
            setValueInternal(new, true)
        }

    override val onChangeEvent = OnChangeEvent()

    ////////////////////////////////////////////////////////////////////////////

    /** Set this property equal to another using arrow notation (a <- b). */
    operator fun compareTo(other: ValueWrapper<T>): Int {
        disconnectBinding()
        connectBinding(other.provider)
        return 0
    }

    override fun equals(other: Any?) = other is MutableProperty<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = "Property(${value.toString()})"

    ////////////////////////////////////////////////////////////////////////////

    private fun disconnectBinding() {
        if (::binding.isInitialized) {
            debug("ktaf.core.verbose") {
                println("Disconnected binding $binding from $this")
            }

            binding.onChangeEvent?.disconnect(bindingFunction)
        }
    }

    private fun connectBinding(other: Value<T>) {
        val onChangeEvent = other.onChangeEvent

        if (onChangeEvent != null) {
            debug("ktaf.core.verbose") {
                println("Connected binding $other to $this")
            }

            bindingFunction = onChangeEvent.connect {
                setValueInternal(other.value, it)
            }
            binding = other
        }

        setValueInternal(other.value, true)
    }

    protected open fun setValueInternal(value: T, animate: Boolean) {
        if (value != rawValue) {
            rawValue = value
            onChangeEvent.emit(true)
        }
    }

    private lateinit var binding: Value<T>
    private lateinit var bindingFunction: (Boolean) -> Unit
}
