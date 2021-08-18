package com.londogard.nlp.meachinelearning.predictors.classifiers

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import org.jetbrains.kotlinx.multik.api.empty
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.ln

// TODO add multi-class Naive Bayes
// https://en.wikipedia.org/wiki/Naive_Bayes_classifier
// This variant uses Laplacian Smoothing
class NaiveBayes: Classifier {
    var logPrior: Float = 0f
    lateinit var logLambda: D2Array<Float> // A 7x1 matrix meaning pre-transposed!

    // Input should be countVectorized etc
    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Int>) { // actually Y can be a D1 array.. or what if dual class?!
        // build frequencies
        val distinctY = y.distinct()
        require(distinctY.size == 2) { "Naïve Bayes currently only support binary prediction" }
        require(distinctY.contains(1) && distinctY.contains(0)) { "Naïve Bayes requires y = 0 && y = 1."}

        val zero = mk.empty<Float, D1>(X.shape[1])
        val one = mk.empty<Float, D1>(X.shape[1])

        for (row in 0 until X.shape[0]) {
            if (y[row, 0] == 1) zero += X[row]
            else one += X[row]
        }
        val sumZero = zero.sum()
        val sumOne = one.sum()

        val zeroNorm = (zero + 1f) / (sumZero + X.shape[1])
        val oneNorm = (one + 1f) / (sumOne + X.shape[1])
        logLambda = (oneNorm / zeroNorm).inplaceOp { ln(it) }.reshape(1, X.shape[1]) as D2Array<Float>
        logPrior = ln(sumOne / sumZero)
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Int> {
        return predictProba(X as D2Array<Float>).map { elem -> if (elem < 0f) 0 else 1 }
    }

    fun predictProba(X: D2Array<Float>): D2Array<Float> {
        return (X dot logLambda).inplaceOp { it + logPrior } as D2Array<Float>
    }
}