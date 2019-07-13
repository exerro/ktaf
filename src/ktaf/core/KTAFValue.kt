package ktaf.core

open class KTAFValue<T>(
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

    fun connect(fn: (T) -> Unit): (T) -> Unit {
        connections.add(fn)
        return fn
    }

    fun connectComparator(fn: (T, T) -> Unit): (T, T) -> Unit {
        connections2.add(fn)
        return fn
    }

    fun disconnect(fn: (T) -> Unit): (T) -> Unit {
        connections.remove(fn)
        return fn
    }

    fun disconnectComparator(fn: (T, T) -> Unit): (T, T) -> Unit {
        connections2.remove(fn)
        return fn
    }

    operator fun <TT: T> invoke(value: TT, init: TT.() -> Unit = {}): TT = set(value, init)
    open fun <TT: T> set(value: TT, init: TT.() -> Unit): TT { init(value); setValue(value); return value }
    fun set(value: T): T = set(value, {})

    private var connections = mutableListOf<(T) -> Any?>()
    private var connections2 = mutableListOf<(T, T) -> Any?>()
}

fun <T> KTAFValue<T>.joinTo(other: KTAFValue<T>) {
    connect(other::setValue)
    other.connect(::setValue)
}
