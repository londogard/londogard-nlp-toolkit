package com.londogard.nlp.meachinelearning.predictors

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

interface BasePredictor<T: Number> {
    fun fit(X: MultiArray<Float, D2>, y: D2Array<T>)
    fun predict(X: MultiArray<Float, D2>): D2Array<T>
}