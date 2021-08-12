package com.londogard.nlp.meachinelearning.optimizer

import com.londogard.nlp.meachinelearning.loss.LogisticLoss
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.abs

class GradientDescent(
    val maxIterations: Int,
    val stepSize: Float,
    val eps: Float = 1e-6f) {

    fun optimize(
        loss: LogisticLoss,
        initWeights: D2Array<Float>,
        X: MultiArray<Float, D2>,
        y: D2Array<Float>
    ): Pair<D2Array<Float>, List<Float>> {
        // TODO optimizer can figure out the yPredicted to save one iteration of predicts!
        tailrec fun opt(prevWeight: D2Array<Float>, losses: List<Float>): Pair<D2Array<Float>, List<Float>> {
            val weights = prevWeight - (loss.gradient(prevWeight, X, y) * stepSize)
            val currentLoss = loss.loss(weights, X, y)

            return if (losses.isNotEmpty() && (abs(currentLoss - losses.last()) < eps || losses.size >= maxIterations)){
                weights to (losses + currentLoss)
            }
            else opt(weights, losses + currentLoss)
        }
        return opt(initWeights, emptyList())
    }
}