package ktaf.graphics

import observables.Signal
import observables.UnitSignal

class StateStack<T> {
    val pushed = Signal<T>()
    val popped = UnitSignal()

    fun push(item: T) = items.add(item).also { pushed.emit(item) }
    fun pop() = items.removeAt(items.size - 1).also { popped.emit() }

    fun <R: Any, L: Any> fold(initial: R, fn: (T) -> L?, acc: (R, L) -> R): R
            = items.mapNotNull(fn).fold(initial, acc)

    fun <R: Any, L: Any> foldOpt(initial: R?, fn: (T) -> L?, acc: (R?, L) -> R?): R?
            = items.mapNotNull(fn).fold(initial, acc)

    private val items: MutableList<T> = mutableListOf()
}
