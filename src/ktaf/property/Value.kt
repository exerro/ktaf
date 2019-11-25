package ktaf.property

import observables.Signal

interface Value<T> {
    val value: T
    val onChangeEvent: OnChangeEvent?

    operator fun unaryMinus() = ValueWrapper(this)
}

typealias OnChangeEvent = Signal<Boolean>
