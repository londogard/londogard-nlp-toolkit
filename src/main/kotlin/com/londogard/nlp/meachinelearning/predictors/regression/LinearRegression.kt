package com.londogard.nlp.meachinelearning.predictors.regression

import com.londogard.nlp.meachinelearning.toDense
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.pow

class SimpleLinearRegression: Regressor {
    var slope: Float = 0f
    var yIntercept: Float = 0f

    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Float>) {
        val XAvg = X.average().toFloat()
        val yAvg = y.average().toFloat()
        val XminAvg = (X - XAvg)
        val yMinAvg = (y - yAvg)
        val variance = XminAvg.map { it.pow(2) }.sum()
        val covariance = (XminAvg * yMinAvg).sum()
        slope = covariance / variance
        yIntercept = yAvg - (slope * XAvg)
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Float> {
        return (X.toDense() * slope) + yIntercept
    }
}