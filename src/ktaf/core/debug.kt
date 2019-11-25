package ktaf.core

typealias DebugType = String

object debug {
    fun enable(type: DebugType) {
        enabled.add(type)
    }

    fun disable(type: DebugType) {
        enabled.remove(type)
    }

    inline operator fun invoke(type: DebugType, fn: () -> Unit) {
        if (enabled.contains(type)) {
            fn()
        }
    }

    inline operator fun invoke(fn: () -> Unit) = debug("ktaf.experimental", fn)

    operator fun invoke() {
        enable("ktaf")
        enable("ktaf.verbose")
        enable("ktaf.core")
        enable("ktaf.core.verbose")
    }

    val enabled: MutableSet<DebugType> = mutableSetOf("ktaf.experimental")
}
