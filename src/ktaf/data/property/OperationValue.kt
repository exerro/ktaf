package ktaf.data.property

import ktaf.data.Value
import ktaf.util.ArrowOverloadRHSInstance

class Operation<T, R>(val fn: (T) -> R) {
    operator fun unaryMinus() = OperationWrapper(this)
}

class OperationValue<in T, R>(
        private val provider: Value<T>,
        private val fn: (T) -> R
): Value<R> {
    override val value get() = fn(provider.value)
    override val onChangeEvent = provider.onChangeEvent

    override fun toString() = "<operation on $provider>"
}

fun <T, R> op(fn: (T) -> R) = Operation(fn)

object Operations {
    fun add(n: Float) = op { x: Float -> x + n }
    fun add(n: Int) = op { x: Int -> x + n }
}

class OperationWrapper<T, R>(
        private val operation: Operation<T, R>
) {
    operator fun <TT> minus(op: Operation<TT, T>) = OperationWrapper<TT, R>(op { input ->
        operation.fn(op.fn(input))
    })

    operator fun minus(value: Value<T>)
            = ArrowOverloadRHSInstance(OperationValue(value, operation.fn) as Value<R>)
}
