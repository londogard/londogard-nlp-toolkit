package com.londogard.nlp.meachinelearning.regularization

import ai.djl.ndarray.NDArray
import org.ejml.simple.SimpleMatrix

class L2(override val lambda: Float): BaseRegularizer {
    override fun reguralize(weights: SimpleMatrix, size: Int): Float {
        return (weights.elementPower(2.0).elementSum() * lambda / (size * 2)).toFloat()
    }

    fun reguralize(weights: NDArray, size: Int): Float {
        return weights.pow(2).sum().toFloatArray()[0] * lambda / (size * 2)
    }
}
