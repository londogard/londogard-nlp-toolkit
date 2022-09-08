package com.londogard.nlp.meachinelearning.predictors.classifiers

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.toDense
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.empty
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.ln

/** A [Naïve Bayes Classifier](https://en.wikipedia.org/wiki/Naive_Bayes_classifier) that is using Laplacian Smoothing. */
class NaiveBayes: Classifier {
    var logPrior: Float = 0f
    lateinit var logLambda: D2Array<Float> // A 7x1 matrix meaning pre-transposed!

    // Input should be countVectorized etc
    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Int>) { // actually Y can be a D1 array.. or what if dual class?!
        // build frequencies
        val distinctY = y.distinct()
        val denseX = X.toDense()
        require(distinctY.size == 2) { "Naïve Bayes currently only support binary prediction" }
        require(distinctY.contains(1) && distinctY.contains(0)) { "Naïve Bayes requires y = 0 && y = 1."}

        val zero = mk.zeros<Float>(denseX.shape[1])
        val one = mk.zeros<Float>(denseX.shape[1])

        for (row in 0 until denseX.shape[0]) {
            if (y[row, 0] == 1) one += denseX[row]
            else zero += denseX[row]
        }
        val sumZero = zero.sum()
        val sumOne = one.sum()

        val zeroNorm = (zero + 1f) / (sumZero + denseX.shape[1])
        val oneNorm = (one + 1f) / (sumOne + denseX.shape[1])
        logLambda = (oneNorm / zeroNorm).inplaceOp { ln(it) }.reshape(denseX.shape[1], 1) as D2Array<Float>
        logPrior = ln(sumOne / sumZero)
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Int> {
        val proba = predictProba(X.toDense())

        return mk.d2array(X.shape[0], 1) { i -> if (proba.data[i] < 0f) 0 else 1 }
    }

    fun predictProba(X: D2Array<Float>): D2Array<Float> {
        return (X dot logLambda) + logPrior
    }
}