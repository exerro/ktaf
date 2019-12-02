package ktaf.data

import ktaf.util.ArrowOverloadRHS
import observables.Signal

interface Value<T>: ArrowOverloadRHS<Value<T>> {
    val value: T
    val onChangeEvent: OnChangeEvent?
}

typealias OnChangeEvent = Signal<Boolean>
