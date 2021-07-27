package com.londogard.nlp.meachinelearning.vectorizer

import space.kscience.kmath.linear.Matrix

// TODO allow file / stream input
interface BaseVectorizer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: Matrix<INPUT>): Unit
    fun transform(input: Matrix<INPUT>): Matrix<OUTPUT>
    fun fitTransform(input: Matrix<INPUT>): Matrix<OUTPUT> {
        fit(input)
        return transform(input)
    }
}