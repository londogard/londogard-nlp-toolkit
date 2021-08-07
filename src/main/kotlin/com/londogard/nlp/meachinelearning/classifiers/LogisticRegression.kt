package com.londogard.nlp.meachinelearning.classifiers

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.loss.LogisticLoss
import com.londogard.nlp.meachinelearning.optimizer.GradientDescent
import com.londogard.nlp.meachinelearning.sigmoid
import org.jetbrains.kotlinx.multik.api.empty
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.operations.map

class LogisticRegression: Classifier {
    private lateinit var weights: D2Array<Float>
    private lateinit var losses: List<Float>

    override fun fit(X: D2Array<Float>, y: D2Array<Int>) {
        weights = mk.empty(1, X.shape[1])

        val optimizer = GradientDescent(1000, 0.01f, 1e-6f)

        // TODO add better typing to allow Int as Y.
        val (weightOut, lossesOut) = optimizer.optimize(LogisticLoss(), weights, X, y.asType())
        weights = weightOut
        losses = lossesOut
    }

    override fun predict(X: D2Array<Float>): D2Array<Int> =
        predictProba(X).map { if (it > 0.5f) 1 else 0 }

    fun predictProba(X: D2Array<Float>): D2Array<Float> =
        (X dot weights.transpose()).sigmoid()
}