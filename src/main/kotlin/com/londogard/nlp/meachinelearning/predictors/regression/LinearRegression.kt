package com.londogard.nlp.meachinelearning.predictors.regression

import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.toDense
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.pow

class SimpleLinearRegression: Regressor {
    var slope: Float = 0f
    var yIntercept: Float = 0f

    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Float>) {
        val XAvg = X.sum() / X.shape[0]
        val yAvg = y.sum() / y.shape[0]
        val XminAvg = (X - XAvg)
        val yMinAvg = (y - XAvg)
        val variance = XminAvg.inplaceOp { it.pow(2) }.sum()
        val covariance = (XminAvg * yMinAvg).sum()
        slope = covariance / variance
        yIntercept = yAvg - (slope * XAvg)
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Float> {
        return X.toDense().map { it * slope + yIntercept }
    }
}