package com.londogard.nlp.meachinelearning.encoders

import org.jetbrains.kotlinx.multik.ndarray.data.*

/**
 * [Encoder] is a way to encode something into numerical categories
 */
interface Encoder<T> {
    fun fit(input: List<T>): Unit
    fun transform(input: List<T>): D2Array<Int>
    fun fitTransform(input: List<T>): D2Array<Int> {
        fit(input)
        return transform(input)
    }
    fun invert(input: D2Array<Int>): List<T>
}