package ktaf.resource

import java.util.concurrent.CyclicBarrier
import kotlin.concurrent.thread

abstract class ResourceLoader<T: Any> {
    abstract fun load(resource: ResourceIdentifier): T

    fun loadInBackground(resource: ResourceIdentifier, fn: (T) -> Unit) {
        thread(start = true, isDaemon = true) {
            fn(load(resource))
        }
    }

    fun loadAll(vararg resources: ResourceIdentifier): List<T> {
        val results: MutableList<T?> = (1 .. resources.size).map { null }.toMutableList()
        val barrier = CyclicBarrier(resources.size + 1)

        resources.forEachIndexed { index, resource ->
            loadInBackground(resource) {
                results[index] = it
                barrier.await()
            }
        }

        barrier.await()

        return results.filterNotNull()
    }

    fun loadAllInBackground(vararg resources: ResourceIdentifier, fn: (List<T>) -> Unit) {
        thread(start = true, isDaemon = true) {
            fn(loadAll(*resources))
        }
    }
}
