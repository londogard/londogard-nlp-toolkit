package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.D2FloatArray

// TODO allow file / stream input
interface Vectorizer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: List<List<String>>): Unit
    fun transform(input: List<List<String>>): D2FloatArray
    fun fitTransform(input: List<List<String>>): D2FloatArray {
        fit(input)
        return transform(input)
    }
}