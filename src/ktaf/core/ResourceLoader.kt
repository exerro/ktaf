package ktaf.core

import java.util.concurrent.CyclicBarrier
import kotlin.concurrent.thread

abstract class ResourceLoader<T: Any> {
    fun get(): T = if (loaded) value else {
        synchronized(lock) { lock.wait() }
        value
    }

    fun get(fn: (T) -> Unit): Unit = if (loaded) fn(value) else {
        thread(start = true, isDaemon = true) {
            synchronized(lock) { lock.wait() }
            fn(value)
        }
        Unit
    }

    protected abstract fun load(): T

    internal fun start() {
        thread(start = true, isDaemon = true) {
            value = load()
            loaded = true
            synchronized(lock) { lock.notifyAll() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private lateinit var value: T
    private val lock = Object()
    private var loaded = false
}
