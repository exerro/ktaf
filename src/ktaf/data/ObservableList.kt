package ktaf.data

import observables.Signal

class ObservableList<T> internal constructor(
        private val items: MutableList<T>,
        val onItemAdded: Signal<Int>,
        val onItemRemoved: Signal<Int>
): MutableList<T> {
    constructor(items: List<T>): this(items.toMutableList(), Signal(), Signal())

    override val size get() = items.size
    override fun contains(element: T) = items.contains(element)
    override fun containsAll(elements: Collection<T>) = items.containsAll(elements)
    override fun get(index: Int) = items[index]
    override fun indexOf(element: T) = items.indexOf(element)
    override fun isEmpty() = items.isEmpty()
    override fun iterator() = items.iterator()
    override fun lastIndexOf(element: T) = items.lastIndexOf(element)
    override fun listIterator() = items.listIterator()
    override fun listIterator(index: Int) = items.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = ObservableList(items.subList(fromIndex, toIndex))

    override fun add(element: T)
            = items.add(element).also { onItemAdded.emit(items.size - 1) }

    override fun add(index: Int, element: T)
            = items.add(index, element).also { onItemAdded.emit(index) }

    override fun addAll(index: Int, elements: Collection<T>)
            = elements.mapIndexed { i, it -> add(index + i, it) }.any()

    override fun addAll(elements: Collection<T>)
            = elements.map(this::add).any()

    override fun clear()
            = items.forEach { remove(it) }

    override fun remove(element: T)
            = indexOf(element).takeIf { it != -1 } ?.let(this::removeAt) != null

    override fun removeAll(elements: Collection<T>)
            = elements.map(this::remove).any()

    override fun removeAt(index: Int)
            = items.removeAt(index).also { onItemRemoved.emit(index) }

    override fun retainAll(elements: Collection<T>)
            = (items - elements).map(this::remove).any()

    override fun set(index: Int, element: T)
            = removeAt(index).also { add(index, element) }

    companion object {
        fun <T> of(vararg items: T) = ObservableList(items.toList())
    }
}
