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

    fun connectAdded(fn: (T) -> Any?): (T) -> Any? {
        addedConnections.add(fn)
        return fn
    }

    fun disconnectAdded(fn: (T) -> Any?): (T) -> Any? {
        addedConnections.remove(fn)
        return fn
    }

    fun connectRemoved(fn: (T) -> Any?): (T) -> Any? {
        removedConnections.add(fn)
        return fn
    }

    fun disconnectRemoved(fn: (T) -> Any?): (T) -> Any? {
        removedConnections.remove(fn)
        return fn
    }

    fun connectChanged(fn: (T) -> Any?): (T) -> Any? {
        addedConnections.add(fn)
        removedConnections.add(fn)
        return fn
    }

    fun disconnectChanged(fn: (T) -> Any?): (T) -> Any? {
        addedConnections.remove(fn)
        removedConnections.remove(fn)
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
        addedConnections.forEach { it(element) }
        return true
    }

    override fun add(index: Int, element: T) {
        value.add(index, element)
        addedConnections.forEach { it(element) }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        value.addAll(index, elements)
        addedConnections.forEach { fn -> elements.forEach { fn(it) } }
        return elements.isNotEmpty()
    }

    override fun addAll(elements: Collection<T>): Boolean {
        value.addAll(elements)
        addedConnections.forEach { fn -> elements.forEach { fn(it) } }
        return elements.isNotEmpty()
    }

    override fun clear() {
        val items = value.map { it }
        value.clear()
        removedConnections.forEach { fn -> items.forEach { fn(it) } }
    }

    override fun remove(element: T): Boolean {
        return if (value.remove(element)) {
            removedConnections.forEach { it(element) }
            true
        } else false
    }

    override fun removeAt(index: Int): T {
        val result = value.removeAt(index)
        removedConnections.forEach { it(result) }
        return result
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val removed = value - elements
        val result = value.retainAll(elements)
        removedConnections.forEach { fn -> removed.forEach { fn(it) } }
        return result
    }

    override fun set(index: Int, element: T): T {
        val old = value.set(index, element)
        removedConnections.forEach { it(old) }
        addedConnections.forEach { it(element) }
        return old
    }

    private var addedConnections = mutableListOf<(T) -> Any?>()
    private var removedConnections = mutableListOf<(T) -> Any?>()
}
