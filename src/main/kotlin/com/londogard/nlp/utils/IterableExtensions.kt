package com.londogard.nlp.utils

object IterableExtensions {
    fun <K> List<K>.identityCount(): Map<K, Int> = groupingBy { it }.eachCount()

    fun Array<String>.ngrams(n: Int): List<String> = when (n) {
        1 -> this.asList()
        else -> asIterable().windowed(n) { it.joinToString(" ") }
    }

    fun List<String>.ngrams(n: Int): List<String> = when (n) {
        1 -> this
        else -> windowed(n) { it.joinToString(" ") }
    }

    fun List<List<String>>.getNgramCountsPerDoc(ngramRange: IntRange): List<Map<String, Int>> =
        (ngramRange).fold(emptyList()) { acc, n ->
            val ngrams = if (n == 1) map { it.identityCount() } else map { it.ngrams(n).identityCount() }

            if (acc.isEmpty()) ngrams else acc.zip(ngrams) { lhs, rhs -> lhs + rhs }
        }

    fun <K, V> List<Map<K, V>>.mergeReduce(reduce: (V, V) -> V): Map<K, V> =
        if (isEmpty()) emptyMap() else first().mergeReduce(subList(1, size), reduce)

    fun <K, V> Map<K, V>.mergeReduce(others: List<Map<K, V>>, reduce: (V, V) -> V): Map<K, V> =
        toMutableMap().apply {
            others.forEach { other ->
                other.forEach { (key, value) ->
                    merge(
                        key,
                        value!!,
                        reduce
                    )
                }
            }
        }

    fun <K, V> MutableMap<K, V>.mergeReduceInPlace(others: List<Map<K, V>>, reduce: (V, V) -> V) =
        others.forEach { other -> other.forEach { (key, value) -> merge(key, value!!, reduce) } }
}