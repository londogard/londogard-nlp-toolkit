package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.inputs.Coordinate
import org.ejml.data.FMatrixSparseCSC
import org.ejml.data.MatrixType.*
import org.ejml.simple.SimpleMatrix

// TODO
class EjmlMatrix<T: Number>(matrix: SimpleMatrix): SimpleMatrix() {
    companion object {
        fun <T: Number> wrap(matrix: SimpleMatrix): EjmlMatrix<T>? = when (matrix.type) {
            FSCC, FDRM -> wrap(matrix.cdrm) as EjmlMatrix<T>
            DSCC, DDRM -> wrap(matrix.cdrm) as EjmlMatrix<T>
            else -> null
        }
    }
}

object SimpleMatrixUtils {
    fun sparseOf(numRows: Int, numCols: Int, elements: List<Coordinate<Float>>): SimpleMatrix =
        SimpleMatrix.wrap(FMatrixSparseCSC(numRows, numCols, elements.size).apply {
            elements.forEach { elem -> set(elem.row, elem.col, elem.count) }
        })
}