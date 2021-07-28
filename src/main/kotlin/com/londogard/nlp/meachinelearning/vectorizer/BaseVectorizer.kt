package com.londogard.nlp.meachinelearning.vectorizer

import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point

// TODO allow file / stream input
interface BaseVectorizer<INPUT: Number, OUTPUT: Number> {
    fun fit(input: List<Point<INPUT>>): Unit
    fun transform(input: List<Point<INPUT>>): Matrix<OUTPUT>
    fun fitTransform(input: List<Point<INPUT>>): Matrix<OUTPUT> {
        fit(input)
        return transform(input)
    }
}