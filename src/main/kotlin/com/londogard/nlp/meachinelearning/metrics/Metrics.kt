package com.londogard.nlp.meachinelearning.metrics

import com.londogard.nlp.meachinelearning.inplaceOp
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.view
import org.jetbrains.kotlinx.multik.ndarray.operations.average
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import kotlin.math.pow

object Metrics {
    fun confusionMatrix() {
        TODO("")
    }

    fun f1() {
        TODO("")
    }

    // precision = TP / (TP + FP)
    // Precision = Sum c in C TruePositives_c / Sum c in C (TruePositives_c + FalsePositives_c)
    fun <T: Number> precision(test: D2Array<T>, predicted: D2Array<T>) {
        TODO("")
    }

    // recall = TP / (TP + FN)
    fun <T: Number> recall(test: D2Array<T>, predicted: D2Array<T>) {
        TODO("")
    }

    fun rSquared(test: D2Array<Float>, predicted: D2Array<Float>): Float {
        val sst = (test - test.average().toFloat()).inplaceOp { it.pow(2) }.sum()
        val ssr = (test - predicted).inplaceOp { it.pow(2) }.sum()

        return (sst - ssr) / sst // R²
    }


    // accuracy = TP + FP / (TP + FP + FN + TN)
    fun <T : Number> accuracy(test: D2Array<T>, predicted: D2Array<T>): Double {
        require(test.shape.toList() == predicted.shape.toList()) {
            "The size of testWords list doesn't match the size of the testTags list"
        }
        var correct = 0
        for (row in 0 until test.shape[0]) {
            if(test.view(row) == predicted.view(row)) { correct += 1 }
        }

        return correct.toDouble() / test.shape[0]
    }
}