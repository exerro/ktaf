package ktaf.data.property

import ktaf.core.debug
import ktaf.data.OnChangeEvent
import ktaf.data.Value
import ktaf.util.ArrowOverloadLHS
import ktaf.util.compareTo

fun <T> mutableProperty(value: T) = MutableProperty(value)

//////////////////////////////////////////////////////////////////////////////////////////

open class MutableProperty<T>(
        protected var rawValue: T
): Value<T>, ArrowOverloadLHS<Value<T>> {
    override var value
        get() = rawValue
        set(new) {
            disconnectBinding()
            setValueInternal(new, true)
        }

    override val onChangeEvent = OnChangeEvent()

    ////////////////////////////////////////////////////////////////////////////

    override fun arrowOperator(value: Value<T>) {
        disconnectBinding()
        connectBinding(value)
    }

    override fun equals(other: Any?) = other is Value<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = "Property(${value.toString()})"

    ////////////////////////////////////////////////////////////////////////////

    protected open fun setValueInternal(value: T, animate: Boolean) {
        if (value != rawValue) {
            rawValue = value
            onChangeEvent.emit(true)
        }
    }

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

    private lateinit var binding: Value<T>
    private lateinit var bindingFunction: (Boolean) -> Unit

    ////////////////////////////////////////////////////////////////////////////

    companion object {
        fun <T> link(a: MutableProperty<T>, b: MutableProperty<T>) {
            a <- b
            b <- a
        }
    }
}
