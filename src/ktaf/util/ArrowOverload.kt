package ktaf.util

interface ArrowOverloadLHS<T, R> {
    fun arrowOperator(value: T): R
}

@Suppress("UNCHECKED_CAST")
interface ArrowOverloadRHS<T> {
    operator fun unaryMinus() = ArrowOverloadRHSInstance(this as T)
}

class ArrowOverloadRHSInstance<T> internal constructor(
        internal val value: T
)

operator fun <T, R> ArrowOverloadLHS<T, R>.compareTo(value: ArrowOverloadRHSInstance<T>): Int {
    arrowOperator(value.value)
    return 0
}
