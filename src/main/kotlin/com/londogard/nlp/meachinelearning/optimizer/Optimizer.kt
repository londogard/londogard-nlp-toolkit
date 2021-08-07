package com.londogard.nlp.meachinelearning.optimizer

import com.londogard.nlp.meachinelearning.loss.Loss
import org.ejml.simple.SimpleMatrix
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array

interface Optimizer {
    fun optimize(loss: Loss, initWeights: D2Array<Float>, X: D2Array<Float>, y: D2Array<Float>): Pair<D2Array<Float>, List<Float>>
}