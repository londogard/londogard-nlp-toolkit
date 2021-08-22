package com.londogard.nlp.meachinelearning.transformers

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

interface Transformer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: MultiArray<Float, D2>): Unit
    fun transform(input: MultiArray<Float, D2>): MultiArray<Float, D2>
    fun fitTransform(input: MultiArray<Float, D2>): MultiArray<Float, D2> {
        fit(input)
        return transform(input)
    }
}