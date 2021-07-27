package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.ndarray.data.*
/**
public inline fun <reified T : Number, D: Dimension> Multik.sparseNdArray(shape: IntArray,
                                                                          numElements: Int,
                                                                          nonZeroIndices: IntArray,
                                                                          noinline init: (Int) -> T): SparseNDArray<T, D> {
    val dtype = DataType.of(T::class)
    for (i in shape.indices) { require(shape[i] > 0) { "Dimension $i must be positive."} }
    val data = initMemoryView<T>(numElements, dtype, init)
    return SparseNDArray<T, D>(data, shape = shape, dtype = dtype, dim = , numElements = numElements, nonZeroIndices = nonZeroIndices)
}*/