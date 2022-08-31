package com.londogard.nlp.meachinelearning.predictors.classifiers

import com.londogard.nlp.meachinelearning.encoders.OneHotEncoder
import com.londogard.nlp.meachinelearning.predictors.BasePredictor
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

class AutoOneHotClassifier<T : BasePredictor<Int>>(val predictor: T) : BasePredictor<Int> by predictor {
    private val oneHotEncoder = OneHotEncoder()

    @JvmName("fitSimple")
    fun fit(X: MultiArray<Float, D2>, y: D1Array<Int>) {
        val yEncoded = oneHotEncoder.fitTransform(y)
        predictor.fit(X, yEncoded)
    }

    fun predictSimple(X: MultiArray<Float, D2>): D1Array<Int> {
        return oneHotEncoder.invert(predictor.predict(X))
    }
}