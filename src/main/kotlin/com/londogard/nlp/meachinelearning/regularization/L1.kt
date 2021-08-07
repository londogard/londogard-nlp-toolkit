package com.londogard.nlp.meachinelearning.regularization

import com.londogard.nlp.utils.map
import org.ejml.simple.SimpleMatrix
import kotlin.math.abs

class L1(override val lambda: Float): BaseRegularizer {
    override fun reguralize(weights: SimpleMatrix, size: Int): Float {
        return (weights.map { abs(it) }.elementSum() * lambda / size).toFloat()
    }
}