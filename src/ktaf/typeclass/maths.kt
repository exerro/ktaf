package ktaf.typeclass

interface Add<T, R> {
    infix fun add(v: T): R
}

interface Sub<T, R> {
    infix fun sub(v: T): R
}

interface Mul<T, R> {
    infix fun mul(v: T): R
}

interface Div<T, R> {
    infix fun div(v: T): R
}

interface Unm<R> {
    fun unm(): R
}

operator fun <T, R> Add<T, R>.plus(other: T): R = add(other)
operator fun <T, R> Sub<T, R>.minus(other: T): R = sub(other)
operator fun <T, R> Mul<T, R>.times(other: T): R = mul(other)
operator fun <T, R> Div<T, R>.div(other: T): R = div(other)
operator fun <R> Unm<R>.unaryMinus(): R = unm()
