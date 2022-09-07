package com.londogard.nlp.meachinelearning.encoders

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import kotlin.properties.Delegates

/**
 * A simple OneHotEncoder which encodes a category of any type into a numeric value.
 */
class OneHotEncoder<T>: Encoder<T> {
    private var mapping by Delegates.notNull<Map<T, Int>>()
    private var reverseMapping by Delegates.notNull<List<T>>()

    override fun fit(input: List<T>) {
        reverseMapping = input.distinct()
        mapping = reverseMapping.withIndex().associate { it.value to it.index }
    }

    override fun transform(input: List<T>): D2Array<Int> {
        val out = mk.zeros<Int>(input.size, mapping.size)
        input.forEachIndexed { index, item -> out[index, mapping.getValue(item)] = 1 }

        return out
    }

    override fun invert(input: D2Array<Int>): List<T> {
        val categories = mk.math.argMaxD2(input, 1)

        return List(categories.size) { i -> reverseMapping[i] }
    }
}