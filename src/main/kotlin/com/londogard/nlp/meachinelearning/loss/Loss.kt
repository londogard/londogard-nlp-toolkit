package com.londogard.nlp.meachinelearning.loss

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set

interface Loss {
    fun loss(weights: D2Array<Float>, X: D2Array<Float>, y: D2Array<Float>): Float
    fun gradient(weights: D2Array<Float>, X: D2Array<Float>, y: D2Array<Float>): D2Array<Float>

    fun numericGradient(weights: D2Array<Float>, X: D2Array<Float>, y: D2Array<Float>, eps: Float = 1e-6f): D2Array<Float> =
        (0 until weights.shape[1])
            .map { i ->
                val oldWeights = weights.clone()
                val newWeights = weights.clone()
                oldWeights[0,i] -= eps
                newWeights[0,i] += eps

                (loss(newWeights, X, y) - loss(oldWeights, X, y)) / (2f * eps)
            }
            .toFloatArray()
            .let { mk.ndarray(it, 1, it.size) }
}