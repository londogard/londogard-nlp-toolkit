package com.londogard.nlp.meachinelearning.optimizer

import ai.djl.ndarray.types.SparseFormat
import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.loss.LogisticLoss
import com.londogard.nlp.meachinelearning.sigmoidFast
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.minusAssign
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.absoluteValue
import kotlin.system.exitProcess

/** A simple implementation of Gradient Descent optimization */
class GradientDescent(
    val maxIterations: Int,
    val stepSize: Float,
    val eps: Float = 1e-6f) {
    val losses = mk.zeros<Float>(maxIterations)
    var i = 0

    fun optimize(
        loss: LogisticLoss,
        initWeights: D2Array<Float>,
        X: MultiArray<Float, D2>,
        y: D2Array<Float>
    ): Pair<D2Array<Float>, D1Array<Float>> {
        val yTransposed = y.transpose()
        val xTransposed = X.transpose()
        val yNegTrans = yTransposed - 1f

        while (i < maxIterations && (i <= 1 || (losses[i] - losses[i - 1]).absoluteValue > eps)) {
            val yPred = (X dot initWeights.transpose()).inplaceOp(::sigmoidFast) as D2Array<Float>
            initWeights -= loss.gradient(yPred, xTransposed, y) * stepSize
            val currentLoss = loss.loss(yPred, X, yTransposed, yNegTrans)
            losses[i] = currentLoss
            i += 1
        }
        return initWeights to losses[0 until i] as D1Array<Float>
    }
}