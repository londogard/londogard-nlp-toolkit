package com.londogard.nlp.utils.caches

typealias Cache<K, V> = MutableMap<K, V>
//interface Cache<K, V> {
//    val size: Int
//
//    val keys: Set<K>
//
//    operator fun set(key: K, value: V)
//
//    operator fun get(key: K): V?
//
//    fun contains(key: K): Boolean
//
//    fun remove(key: K): V?
//
//    fun clear()
//}