package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.inputs.Coordinate
import org.ejml.data.FMatrixSparseCSC
import org.ejml.data.MatrixType.*
import org.ejml.simple.SimpleMatrix

object SimpleMatrixUtils {
    fun sparseOf(numRows: Int, numCols: Int, elements: List<Coordinate<Float>>): SimpleMatrix =
        SimpleMatrix.wrap(FMatrixSparseCSC(numRows, numCols, elements.size).apply {
            elements.forEach { elem -> set(elem.row, elem.col, elem.count) }
        })
}