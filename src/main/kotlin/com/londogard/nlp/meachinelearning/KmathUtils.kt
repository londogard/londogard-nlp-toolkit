package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.inputs.Coordinate
import org.ejml.data.DMatrixSparse
import org.ejml.data.FMatrixSparse
import org.ejml.data.FMatrixSparseCSC
import space.kscience.kmath.ejml.EjmlDoubleMatrix
import space.kscience.kmath.ejml.EjmlFloatMatrix
import space.kscience.kmath.ejml.EjmlLinearSpaceFSCC
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.operations.FloatField
import space.kscience.kmath.structures.asIterable

object KmathUtils {
    fun efficientSparseBuildMatrix(
        rows: Int,
        columns: Int,
        size: Int,
        initializer: FloatField.(i: Int, j: Int) -> Float,
    ): EjmlFloatMatrix<FMatrixSparseCSC> =
        EjmlFloatMatrix(FMatrixSparseCSC(rows, columns, size).also {
            (0 until rows).forEach { row ->
                (0 until columns).forEach { col ->
                    val value = EjmlLinearSpaceFSCC.elementAlgebra.initializer(row, col)
                    if (value != 0f) {
                        it[row, col] = value
                    }
                }
            }
        })
    fun efficientSparseBuildMatrix(
        rows: Int,
        columns: Int,
        size: Int,
        initializer: List<Coordinate<Float>>,
    ): EjmlFloatMatrix<FMatrixSparseCSC> =
        EjmlFloatMatrix(FMatrixSparseCSC(rows, columns, size).also {
            initializer.forEach { coordinate ->
                it[coordinate.row, coordinate.col] = coordinate.count
            }
        })

    // fun Matrix<Double>.toSparse(): Matrix<Double> = when {
    //     this is EjmlDoubleMatrix<*> && origin is DMatrixSparse -> this
    //     else -> {
    //         val numNzElems = rows.sumOf { row -> row.asIterable().count { it > 0 } }
    //         efficientSparseBuildMatrix(rowNum, colNum, numNzElems) { x, y -> get(x, y) }
    //     }
    // }

    fun Matrix<Float>.toSparse(): Matrix<Float> = when {
        this is EjmlFloatMatrix<*> && origin is FMatrixSparse -> this
        else -> {
            val numNzElems = rows.sumOf { row -> row.asIterable().count { it > 0 } }
            efficientSparseBuildMatrix(rowNum, colNum, numNzElems) { x, y -> get(x, y) }
        }
    }

    fun <T> Matrix<T>.numElements(): Int = when {
        this is EjmlFloatMatrix<*> && origin is FMatrixSparse -> origin.numElements
        this is EjmlDoubleMatrix<*> && origin is DMatrixSparse -> origin.numElements
        else -> colNum * rowNum
    }
}