package ktaf.core

class KTAFValue<T>(
        private val value: T
) {
    fun get(): T = value
}

open class KTAFMutableValue<T>(
        private var value: T
) {
    fun get(): T = value
    fun setValue(value: T) {
        val oldValue = this.value
        if (this.value != value) {
            this.value = value
            connections.forEach { it(value) }
            connections2.forEach { it(oldValue, value) }
        }
    }

    fun connect(fn: (T) -> Unit) {
        connections.add(fn)
    }

    fun connectComparator(fn: (T, T) -> Unit) {
        connections2.add(fn)
    }

    fun disconnect(fn: (T) -> Unit) {
        connections.remove(fn)
    }

    fun disconnectComparator(fn: (T, T) -> Unit) {
        connections2.remove(fn)
    }

    operator fun <TT: T> invoke(value: TT, init: TT.() -> Unit = {}): TT = set(value, init)
    open fun <TT: T> set(value: TT, init: TT.() -> Unit = {}): TT { init(value); setValue(value); return value }

    private var connections = mutableListOf<(T) -> Any?>()
    private var connections2 = mutableListOf<(T, T) -> Any?>()
}

fun <T> KTAFMutableValue<T>.joinTo(other: KTAFMutableValue<T>) {
    connect(other::setValue)
    other.connect(::setValue)
}
