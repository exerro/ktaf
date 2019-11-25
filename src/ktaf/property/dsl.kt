package ktaf.property

class OperationBuilder<T, R> internal constructor(
        private val fn: (T) -> R
) {
    operator fun <A> minus(fn: (A) -> T) = OperationBuilder<A, R> {
        this.fn(fn(it))
    }

    operator fun minus(provider: Value<T>) = ValueWrapper(Operation(provider, fn))
}

class ValueWrapper<T>internal constructor(internal val provider: Value<T>)

operator fun <T, R> ((T) -> R).unaryMinus() = OperationBuilder(this)
