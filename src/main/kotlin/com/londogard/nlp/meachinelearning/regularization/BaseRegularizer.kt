package com.londogard.nlp.meachinelearning.regularization

import org.ejml.simple.SimpleMatrix

interface BaseRegularizer {
    val lambda: Float
    fun regularize(weights: SimpleMatrix, size: Int): Float
}