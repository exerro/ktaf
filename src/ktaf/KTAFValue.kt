package ktaf

class KTAFValue<T>(
        private val value: T
) {
    fun get(): T = value
}

class KTAFList<T>(
        private val value: List<T> = listOf()
): List<T> {
    override val size: Int get() = value.size
    override fun contains(element: T): Boolean = value.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = value.containsAll(elements)
    override fun get(index: Int): T = value[index]
    override fun indexOf(element: T): Int = value.indexOf(element)
    override fun isEmpty(): Boolean = value.isEmpty()
    override fun iterator(): Iterator<T> = value.iterator()
    override fun lastIndexOf(element: T): Int = value.lastIndexOf(element)
    override fun listIterator(): ListIterator<T> = value.listIterator()
    override fun listIterator(index: Int): ListIterator<T> = value.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): KTAFList<T> = KTAFList(value.subList(fromIndex, toIndex))
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

    open fun set(value: T) { setValue(value) }

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

    private var connections = mutableListOf<(T) -> Any?>()
    private var connections2 = mutableListOf<(T, T) -> Any?>()
}

class KTAFMutableList<T>(
        private val value: MutableList<T> = mutableListOf()
): MutableList<T> {
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
    override fun subList(fromIndex: Int, toIndex: Int): KTAFMutableList<T> = KTAFMutableList(value.subList(fromIndex, toIndex))

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

    fun connectAdded(fn: (T) -> Any?) {
        addedConnections.add(fn)
    }

    fun disconnectAdded(fn: (T) -> Any?) {
        addedConnections.remove(fn)
    }

    fun connectRemoved(fn: (T) -> Any?) {
        removedConnections.add(fn)
    }

    fun disconnectRemoved(fn: (T) -> Any?) {
        removedConnections.remove(fn)
    }

    private var addedConnections = mutableListOf<(T) -> Any?>()
    private var removedConnections = mutableListOf<(T) -> Any?>()
}
