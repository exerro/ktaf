package ktaf.core

import observables.Subscribable

typealias Seconds = Float

val Float.s: Seconds get() = this
val Float.ms: Seconds get() = this / 1000f
val Int.s: Seconds get() = toFloat().s
val Int.ms: Seconds get() = toFloat().ms

class Timer {
    val update = Subscribable<Seconds>()

    fun queue(delay: Seconds, fn: () -> Unit) {
        synchronized(queue) { queue.add(delay to fn) }
    }

    fun countdown(duration: Seconds, fn: (Float) -> Unit) {
        synchronized(countdowns) { countdowns.add(duration to fn) }
    }

    fun update(dt: Seconds) {
        update.emit(dt)
        synchronized(queue) {
            queue.replaceAll { (delay, fn) -> delay - dt to fn }
            queue.removeAll { (delay, fn) -> (delay <= 0).also { if (it) fn() } }
        }
        synchronized(countdowns) {
            countdowns.replaceAll { (duration, fn) -> fn(duration); duration - dt to fn }
            countdowns.removeAll { (duration, _) -> duration <= 0f }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private val queue: MutableList<Pair<Seconds, () -> Unit>> = mutableListOf()
    private val countdowns: MutableList<Pair<Seconds, (Float) -> Unit>> = mutableListOf()
}
