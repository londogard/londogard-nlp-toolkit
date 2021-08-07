package com.londogard.nlp.meachinelearning.regression

import com.londogard.nlp.utils.map
import org.ejml.simple.SimpleMatrix

class SimpleLinearRegression {
    var slope: Double = 0.0
    var yIntercept: Double = 0.0

    fun fit(X: SimpleMatrix, y: SimpleMatrix) {
        val XAvg = X.elementSum() / X.numRows()
        val yAvg = y.elementSum() / y.numRows()
        val XminAvg = (X - XAvg)
        val yMinAvg = (y - XAvg)
        val variance = XminAvg.elementPower(2.0).elementSum()
        val covariance = XminAvg.mult(yMinAvg).elementSum()
        slope = covariance / variance
        yIntercept = yAvg - (slope * XAvg)
    }

    fun predict(X: SimpleMatrix): SimpleMatrix {
        return X.map { (it * slope + yIntercept).toFloat() }
    }

    /** Evaluation
     * // SST
    val sst = ys.sumByDouble { y -> (y - ys.average()).pow(2) }
    // SSR
    val ssr = xs.zip(ys) { x, y -> (y - simpleLinearRegression.invoke(x.toDouble())).pow(2) }.sum()
    // R²
    val rsquared = (sst - ssr) / sst
     */
}