package com.londogard.nlp.meachinelearning.encoders

import org.jetbrains.kotlinx.multik.ndarray.data.*


interface Encoder {
    fun fit(input: D1Array<Int>): Unit
    fun transform(input: D1Array<Int>): D2Array<Int>
    fun fitTransform(input: D1Array<Int>): D2Array<Int> {
        fit(input)
        return transform(input)
    }
    fun invert(input: D2Array<Int>): D1Array<Int>
}