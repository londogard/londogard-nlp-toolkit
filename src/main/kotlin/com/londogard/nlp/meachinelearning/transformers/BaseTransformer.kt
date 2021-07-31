package com.londogard.nlp.meachinelearning.transformers

import org.ejml.simple.SimpleMatrix

interface BaseTransformer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: SimpleMatrix): Unit
    fun transform(input: SimpleMatrix): SimpleMatrix
    fun fitTransform(input: SimpleMatrix): SimpleMatrix {
        fit(input)
        return transform(input)
    }
}