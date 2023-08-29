package me.qoomon.examples

class LazyMap<K, out V>(private val lazyMap: Map<K, Lazy<V>>) : Map<K, V> {

    private fun Map<K, Lazy<V>>.initializeAll() = mapValues { it.value.value }

    override val size: Int
        get() = lazyMap.size

    override val keys: Set<K>
        get() = lazyMap.keys

    override val values: Collection<V>
        get() = LazyCollection(lazyMap.values)

    override val entries: Set<Map.Entry<K, V>>
        get() = lazyMap.initializeAll().entries

    override fun isEmpty() = lazyMap.isEmpty()

    override fun get(key: K): V? = lazyMap[key]?.value

    override fun containsValue(value: @UnsafeVariance V): Boolean {
        return lazyMap.initializeAll().containsValue(value)
    }

    override fun containsKey(key: K) = lazyMap.containsKey(key)

    override fun getOrDefault(key: K, defaultValue: @UnsafeVariance V) = get(key) ?: defaultValue
}

class LazyCollection<V>(private val lazyCollection: Collection<Lazy<V>>) : Collection<V> {

    override val size: Int
        get() = lazyCollection.size

    override fun isEmpty() = lazyCollection.isEmpty()

    override fun iterator(): Iterator<V> = LazyIterator(lazyCollection.iterator())

    override fun containsAll(elements: Collection<@UnsafeVariance V>) =
        lazyCollection.map { it.value }.containsAll(elements)

    override fun contains(element: @UnsafeVariance V) =
        lazyCollection.map { it.value }.contains(element)
}

@Suppress("IteratorNotThrowingNoSuchElementException")
class LazyIterator<V>(private val lazyIterator: Iterator<Lazy<V>>) : Iterator<V> {

    override fun hasNext() = lazyIterator.hasNext()


    override fun next(): V = lazyIterator.next().value
}

fun main() {
    val lazyMap = LazyMap(
        mapOf(
            "a" to lazy { 1.also { println("init a") } },
            "b" to lazy { 2.also { println("init b") } },
            "c" to lazy { 3.also { println("init c") } },
            "d" to lazy { 4.also { println("init d") } },
        ),
    )

    println(lazyMap["c"])
    println(lazyMap["a"])
}
