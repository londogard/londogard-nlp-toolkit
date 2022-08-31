package com.londogard.nlp.meachinelearning.encoders

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.forEachIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.max
import kotlin.properties.Delegates

class OneHotEncoder: Encoder {
    private var yMax by Delegates.notNull<Int>()

    override fun fit(input: D1Array<Int>) {
        yMax = (input.max() ?: 0) + 1 // minimum of 2 classes = [0,1]
    }

    override fun transform(input: D1Array<Int>): D2Array<Int> {
        val out = mk.zeros<Int>(input.shape[0], yMax)
        input.forEachIndexed { index, i -> out[index, i] = 1 }

        return out
    }

    override fun invert(input: D2Array<Int>): D1Array<Int> {
        return mk.math.argMaxD2(input, 1)
    }
}