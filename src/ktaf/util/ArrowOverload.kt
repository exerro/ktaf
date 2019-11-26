package ktaf.util

interface ArrowOverloadLHS<T> {
    fun arrowOperator(value: T)
}

@Suppress("UNCHECKED_CAST")
interface ArrowOverloadRHS<T> {
    operator fun unaryMinus() = ArrowOverloadRHSInstance(this as T)
}

class ArrowOverloadRHSInstance<T> internal constructor(
        internal val value: T
)

operator fun <T> ArrowOverloadLHS<T>.compareTo(value: ArrowOverloadRHSInstance<T>): Int {
    arrowOperator(value.value)
    return 0
}
