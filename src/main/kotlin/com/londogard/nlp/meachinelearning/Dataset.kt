package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toSet

class Dataset<INPUT: Number, OUTPUT: Number, D: Dimension>(val X: MultiArray<INPUT, D>, val y: MultiArray<OUTPUT, D>) {
    private val regressionDataTypes = setOf(DataType.FloatDataType, DataType.DoubleDataType)

    init {
        require(X.shape.size > 1) { "X is not allowed to be 1D in Dataset" }
    }

    val numFeatures: Int by lazy { X.shape.last() }
    val numSamples: Int by lazy { X.shape.first() }
    val numClasses: Int by lazy {
        if (y.dtype in regressionDataTypes) throw UnsupportedOperationException("Regression Data has no classes!")
        else when (y.shape.size) {
            1 -> y.toSet().size
            2 -> if (y.shape.contains(1)) y.toSet().size else y.shape.last()
            else -> y.shape.last()
        }
    }
}