package ktaf.property

class Operation<T, R>(
        private val provider: Value<T>,
        private val fn: (T) -> R
): Value<R> {
    override val value get() = fn(provider.value)
    override val onChangeEvent = provider.onChangeEvent

    override fun toString() = "<operation on $provider>"
}

object Operations {
    fun add(n: Float) = { x: Float -> x + n }
    fun add(n: Int) = { x: Int -> x + n }
}
