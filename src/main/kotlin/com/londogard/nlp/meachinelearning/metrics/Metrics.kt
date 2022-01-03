package com.londogard.nlp.meachinelearning.metrics

import com.londogard.nlp.meachinelearning.inplaceOp
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.compareTo
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.average
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import kotlin.math.pow

object Metrics {
    fun rSquared(test: D2Array<Float>, predicted: D2Array<Float>): Float {
        requireSameShape(test, predicted)
        val sst = (test - test.average().toFloat()).inplaceOp { it.pow(2) }.sum()
        val ssr = (test - predicted).inplaceOp { it.pow(2) }.sum()

        return (sst - ssr) / sst // R²
    }

    // accuracy = TP + FP / (TP + FP + FN + TN)
    fun <T : Number> accuracy(test: D2Array<T>, predicted: D2Array<T>): Double {
        requireSameShape(test, predicted)
        val correctPredictions = (0 until test.shape[0]).fold(0) {
                acc, row ->
            if (test[row] == predicted[row]) acc + 1 else acc
        }

        return  correctPredictions / test.shape[0].toDouble()
    }

    private fun <T : Number> requireSameShape(test: D2Array<T>, predicted: D2Array<T>) =
        require(test.shape.contentEquals(predicted.shape)) {
            "The size of testWords list doesn't match the size of the testTags list"
        }
}