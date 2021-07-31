package com.londogard.nlp.meachinelearning.vectorizer

import org.ejml.simple.SimpleMatrix

// TODO allow file / stream input
interface BaseVectorizer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: List<Array<String>>): Unit
    fun transform(input: List<Array<String>>): SimpleMatrix
    fun fitTransform(input: List<Array<String>>): SimpleMatrix {
        fit(input)
        return transform(input)
    }
}