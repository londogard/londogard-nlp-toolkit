package com.londogard.nlp.meachinelearning.vectorizer.native

import com.londogard.nlp.meachinelearning.native.NativeNDArray


interface NativeVectorizer<T: Number> {
    fun fit(input: List<List<String>>): Unit
    fun transform(input: List<List<String>>): NativeNDArray
    fun fitTransform(input: List<List<String>>): NativeNDArray {
        fit(input)
        return transform(input)
    }
}