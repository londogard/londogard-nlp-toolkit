package com.londogard.nlp.utils

import kotlin.math.roundToInt

/**
 * Custom extension functions for Map(s).
 */
object MapExtensions {
    fun Map<String, Float>.toVocab(): Map<String, Int> {
        val min = this.values.minOrNull() ?: 1f
        val scale = (1 / min).roundToInt()
        return this.mapValues { (_, value) -> (value * scale).roundToInt() }
    }

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