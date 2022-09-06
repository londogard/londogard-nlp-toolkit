package com.londogard.nlp.meachinelearning.encoders

import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.forEachIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.max
import kotlin.properties.Delegates

class OneHotEncoder<T>: Encoder<T> {
    private var mapping by Delegates.notNull<Map<T, Int>>()
    private val reverseMapping by Delegates.notNull<D1Array<T>>()

    override fun fit(input: D1Array<T>) {
        val data = input.data.distinct()
        mapping = data.withIndex().associate { it.value to it.index }
        reverseMapping = mk.d1array<T::class>(data.size) { i -> data[i] }
    }

    override fun transform(input: D1Array<T>): D2Array<Int> {
        val out = mk.zeros<Int>(input.shape[0], mapping.size)
        input.forEachIndexed { index, item -> out[index, mapping.getValue(item)] = 1 }

        return out
    }

    override fun invert(input: D2Array<Int>): D1Array<T> {
        return mk
    }
}