package ktaf.core

open class KTAFValue<T>(
        private var value: T
) {
    fun get(): T = value
    fun setValue(value: T) {
        val oldValue = this.value
        if (this.value != value) {
            setting = false
            this.value = value
            for (it in connections) { if (setting) break; it(value) }
            for (it in connections2) { if (setting) break; it(oldValue, value) }
            setting = true
        }
    }

    fun connect(fn: (T) -> Unit): (T) -> Unit {
        connections.add(fn)
        fn(value)
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
    open fun <TT: T> set(value: TT, init: TT.() -> Unit = {}): TT { init(value); setValue(value); return value }
    fun <TT: T> setter(value: TT) { set(value, {}) }

    override fun equals(other: Any?): Boolean = value == other || other is KTAFValue<*> && other == other.value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value.toString()

    private var connections = mutableListOf<(T) -> Any?>()
    private var connections2 = mutableListOf<(T, T) -> Any?>()
    private var setting = false
}

fun <T> KTAFValue<T>.joinTo(other: KTAFValue<T>) {
    connect(other::setter)
    other.connect(::setter)
}
