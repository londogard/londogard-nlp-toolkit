package com.londogard.nlp.utils.caches

class LRUCache<K, V>(private val delegate: Cache<K, V>, private val minimalSize: Int = DEFAULT_SIZE) : Cache<K, V> by delegate {
    private val keyMap = object : LinkedHashMap<K, Boolean>(minimalSize, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, Boolean>): Boolean {
            val tooManyCachedItems = size > minimalSize
            if (tooManyCachedItems) eldestKeyToRemove = eldest.key
            return tooManyCachedItems
        }
    }

    private var eldestKeyToRemove: K? = null


    override fun put(key: K, value: V): V? {
        delegate[key] = value
        cycleKeyMap()
        TODO("Not yet implemented")
    }
    override fun set(key: K, value: V) {
        delegate[key] = value
        cycleKeyMap(key)
    }

    override fun get(key: K): V? {
        keyMap[key]
        return delegate[key]
    }

    override fun clear() {
        keyMap.clear()
        delegate.clear()
    }

    private fun cycleKeyMap(key: K) {
        keyMap[key] = PRESENT
        eldestKeyToRemove?.let { delegate.remove(it) }
        eldestKeyToRemove = null
    }

    companion object {
        private const val DEFAULT_SIZE = 100
        private const val PRESENT = true

        fun <K, V> ofMap(map: HashMap<K, V>): LRUCache<K, V> {
            val delegate = PerpetualCache(map)

            return LRUCache(delegate, minimalSize = DEFAULT_SIZE).apply { keyMap.putAll(delegate.keys.zip(List(delegate.size) { true })) }
        }
    }
}