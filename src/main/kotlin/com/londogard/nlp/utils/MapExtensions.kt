package com.londogard.nlp.utils

import kotlin.math.roundToInt

object MapExtensions {
    fun Map<String, Float>.toVocab(): Map<String, Int> {
        val min = this.values.minOrNull() ?: 1f
        val scale = (1 / min).roundToInt()
        return this.mapValues { (_, value) -> (value * scale).roundToInt() }
    }
}