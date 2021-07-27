package com.londogard.nlp.meachinelearning.transformers

import org.ejml.data.DMatrixSparse
import org.ejml.data.FMatrixSparse
import org.ejml.data.FMatrixSparseCSC
import space.kscience.kmath.ejml.EjmlDoubleMatrix
import space.kscience.kmath.ejml.EjmlFloatMatrix
import space.kscience.kmath.ejml.EjmlLinearSpaceFSCC
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.FloatField
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.asIterable
import kotlin.math.ln

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

fun Structure2D<Float>.efficientSparse(): Structure2D<Float> = when {
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


class TfIdfTransformer() : BaseTransformer<Float, Float> {
    fun transformWithInternal(input: Structure2D<Float>): Pair<Matrix<Float>, Point<Float>> {
        val inputSparse = input.efficientSparse()
        val numDocs = input.rowNum

        val idf = inputSparse
            .columns
            .map { col -> col.asIterable().count { it > 0 } }
            .map { inNumDocs -> ln(numDocs.toFloat() / (inNumDocs + 1)) + 1 }
            .toFloatArray().asBuffer()

        val tfidf = efficientSparseBuildMatrix(input.rowNum, input.colNum, inputSparse.numElements()) {
            i, j -> inputSparse[i,j] * idf[j]
        }

        return tfidf to idf
    }

    /** Input is a count matrix */
    override fun transform(input: Structure2D<Float>): Structure2D<Float> {
        return transformWithInternal(input).first
    }
}