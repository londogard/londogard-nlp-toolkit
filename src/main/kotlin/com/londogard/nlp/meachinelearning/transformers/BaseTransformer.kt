package com.londogard.nlp.meachinelearning.transformers

import space.kscience.kmath.linear.Matrix

interface BaseTransformer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: Matrix<INPUT>): Unit
    fun transform(input: Matrix<INPUT>): Matrix<OUTPUT>
    fun fitTransform(input: Matrix<INPUT>): Matrix<OUTPUT> {
        fit(input)
        return transform(input)
    }
}