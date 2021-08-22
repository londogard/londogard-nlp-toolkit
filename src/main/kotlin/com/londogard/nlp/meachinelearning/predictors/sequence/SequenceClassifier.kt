package com.londogard.nlp.meachinelearning.predictors.sequence

import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

interface SequenceClassifier<T : Number> {
    // Using List<> as the input can be of different sizes between examples
    fun fit(X: List<D1Array<T>>, y: List<D1Array<Int>>)
    fun predict(X: List<D1Array<T>>): List<D1Array<Int>>
}