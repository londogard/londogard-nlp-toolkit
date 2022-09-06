package com.londogard.nlp.meachinelearning.datatypes

/** [Coordinate] is a simple class to wrap numbers in instantiation of a Sparse Matrix */
data class Coordinate<T: Number>(val row: Int, val col: Int, val count: T)