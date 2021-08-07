package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.ndarray.data.D2Array

interface BasePredictor<T: Number> {
    fun fit(X: D2Array<Float>, y: D2Array<T>)

    fun predict(X: D2Array<Float>): D2Array<T>
}