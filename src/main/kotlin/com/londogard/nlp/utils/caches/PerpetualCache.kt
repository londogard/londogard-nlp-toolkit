package com.londogard.nlp.utils.caches

class PerpetualCache<K, V>(
    private val cache: HashMap<K, V> = HashMap()
) : Cache<K, V> by cache