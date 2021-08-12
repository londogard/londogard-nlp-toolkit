package com.londogard.nlp.meachinelearning.transformers.native

import ai.djl.ndarray.NDArray

interface NativeTransformer {
    fun fit(input: NDArray): Unit
    fun transform(input: NDArray): NDArray
    fun fitTransform(input: NDArray): NDArray {
        fit(input)
        return transform(input)
    }
}