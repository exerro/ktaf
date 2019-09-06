package ktaf.core

class KTAFList<T>(
        private val value: MutableList<T> = mutableListOf()
): MutableList<T> {
    fun <TT: T> add(element: TT, init: TT.() -> Unit): TT {
        init(element)
        add(element)
        return element
    }

    fun <TT: T> add(index: Int, element: TT, init: TT.() -> Unit): TT {
        init(element)
        add(index, element)
        return element
    }

    fun <R> connectAddedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        addedConnections.add(fn)
        forEachIndexed { i, v -> fn(i, v) }
        return fn
    }

    fun <R> disconnectAddedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        addedConnections.remove(fn)
        return fn
    }

    fun <R> connectRemovedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        removedConnections.add(fn)
        return fn
    }

    fun <R> disconnectRemovedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        removedConnections.remove(fn)
        return fn
    }

    fun <R> connectChangedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        addedConnections.add(fn)
        removedConnections.add(fn)
        forEachIndexed { i, v -> fn(i, v) }
        return fn
    }

    fun <R> disconnectChangedIndexed(fn: (Int, T) -> R): (Int, T) -> R {
        addedConnections.remove(fn)
        removedConnections.remove(fn)
        return fn
    }

    fun <R> connectAdded(fn: (T) -> R): (T) -> R {
        addedConnections.add { _, item -> fn(item) }
        forEach { fn(it) }
        return fn
    }

    fun <R> disconnectAdded(fn: (T) -> R): (T) -> R {
        addedConnections.remove { _, item -> fn(item) }
        return fn
    }

    fun <R> connectRemoved(fn: (T) -> R): (T) -> R {
        removedConnections.add { _, item -> fn(item) }
        return fn
    }

    fun <R> disconnectRemoved(fn: (T) -> R): (T) -> R {
        removedConnections.remove { _, item -> fn(item) }
        return fn
    }

    fun <R> connectChanged(fn: (T) -> R): (T) -> R {
        addedConnections.add { _, item -> fn(item) }
        removedConnections.add { _, item -> fn(item) }
        forEach { fn(it) }
        return fn
    }

    fun <R> disconnectChanged(fn: (T) -> R): (T) -> R {
        addedConnections.remove { _, item -> fn(item) }
        removedConnections.remove { _, item -> fn(item) }
        return fn
    }

    override val size: Int get() = value.size
    override fun contains(element: T): Boolean = value.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = value.containsAll(elements)
    override fun get(index: Int): T = value[index]
    override fun indexOf(element: T): Int = value.indexOf(element)
    override fun isEmpty(): Boolean = value.isEmpty()
    override fun iterator(): MutableIterator<T> = value.iterator()
    override fun lastIndexOf(element: T): Int = value.lastIndexOf(element)
    override fun listIterator(): MutableListIterator<T> = value.listIterator()
    override fun listIterator(index: Int): MutableListIterator<T> = value.listIterator(index)
    override fun removeAll(elements: Collection<T>): Boolean = elements.map { remove(it) } .any { it }
    override fun subList(fromIndex: Int, toIndex: Int): KTAFList<T> = KTAFList(value.subList(fromIndex, toIndex))

    override fun add(element: T): Boolean {
        value.add(element)
        addedConnections.forEach { it(size - 1, element) }
        return true
    }

    override fun add(index: Int, element: T) {
        value.add(index, element)
        addedConnections.forEach { it(index, element) }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val sizeBefore = size
        value.addAll(index, elements)
        addedConnections.forEach { fn -> elements.forEachIndexed { index, item -> fn(sizeBefore + index, item) } }
        return elements.isNotEmpty()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return addAll(size, elements)
    }

    override fun clear() {
        val items = value.map { it }
        value.clear()
        removedConnections.forEach { fn -> items.forEachIndexed { index, item -> fn(index, item) } }
    }

    override fun remove(element: T): Boolean {
        val index = value.indexOf(element)
        return if (index != -1) {
            value.removeAt(index)
            removedConnections.forEach { it(index, element) }
            true
        } else false
    }

    override fun removeAt(index: Int): T {
        val result = value.removeAt(index)
        removedConnections.forEach { it(index, result) }
        return result
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val removed = (value - elements).let { items -> items.map { Pair(it, value.indexOf(it)) } }
        val result = value.retainAll(elements)
        removedConnections.forEach { fn -> removed.forEach { (item, index) -> fn(index, item) } }
        return result
    }

    override fun set(index: Int, element: T): T {
        val old = value.set(index, element)
        removedConnections.forEach { it(index, old) }
        addedConnections.forEach { it(index, element) }
        return old
    }

    private var addedConnections = mutableListOf<(Int, T) -> Any?>()
    private var removedConnections = mutableListOf<(Int, T) -> Any?>()
}
