package com.londogard.nlp.meachinelearning.predictors.classifiers

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.loss.LogisticLoss
import com.londogard.nlp.meachinelearning.optimizer.GradientDescent
import com.londogard.nlp.meachinelearning.sigmoidFast
import com.londogard.nlp.meachinelearning.toDense
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

class LogisticRegression: Classifier {
    private lateinit var weights: D2Array<Float>
    private lateinit var losses: List<Float>

    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Int>) {
        weights = mk.zeros(1, X.shape[1])

        val optimizer = GradientDescent(1000, 0.01f, 1e-6f)

        val (weightOut, lossesOut) = optimizer.optimize(LogisticLoss(), weights, X, y.asType())
        weights = weightOut
        losses = lossesOut
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Int> {
        val proba = predictProba(X.toDense())

        return mk.d2array(X.shape[0], 1) { i -> if (proba.data[i] < 0.5f) 0 else 1 }
    }

    fun predictProba(X: MultiArray<Float, D2>): MultiArray<Float, D2> =
        (X dot weights.transpose()).inplaceOp(::sigmoidFast)
}