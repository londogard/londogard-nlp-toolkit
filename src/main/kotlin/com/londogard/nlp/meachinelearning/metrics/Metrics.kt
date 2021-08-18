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

    fun rSquared(testData: D2Array<Float>, predictedData: D2Array<Float>): Float {
        val sst = (testData - testData.average().toFloat()).inplaceOp { it.pow(2) }.sum()
        val ssr = (testData - predictedData).inplaceOp { it.pow(2) }.sum()

        return (sst - ssr) / sst // R²
    }


    fun <T : Number> accuracy(testData: D2Array<T>, predictedData: D2Array<T>): Double {
        require(testData.shape.toList() == predictedData.shape.toList()) {
            "The size of testWords list doesn't match the size of the testTags list"
        }
        var correct = 0
        for (row in 0 until testData.shape[0]) {
            if(testData.view(row) == predictedData.view(row)) { correct += 1 }
        }

        return correct.toDouble() / testData.shape[0]
    }
}