package com.londogard.nlp.utils

import org.ejml.data.FMatrixRMaj
import org.ejml.data.MatrixType
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.NormOps_FDRM
import org.ejml.kotlin.*
import org.ejml.simple.SimpleMatrix

/**
 * Custom extensions for EJML simplification in Kotlin. Some optimized for speed.
 */

/** Basic Retrieval */
fun SimpleMatrix.getRow(index: Int): SimpleMatrix = extractVector(true, index)
fun SimpleMatrix.getRows(rows: IntArray): SimpleMatrix =
    SimpleMatrix(CommonOps_FDRM.extract(fdrm, rows, numCols(), null))

fun SimpleMatrix.sumCols(): SimpleMatrix = SimpleMatrix(CommonOps_FDRM.sumCols(fdrm, null))

/** Distance Algorithms */
fun SimpleMatrix.euclideanDistance(other: SimpleMatrix): Double = (this - other).normF()
fun SimpleMatrix.cosineDistance(other: SimpleMatrix): Double = this.dot(other) / (this.fastNormF() * other.fastNormF())

/** Fast Normalizers: OBS prone to overflows/underflows */
fun SimpleMatrix.fastNormF(): Float = NormOps_FDRM.fastNormF(fdrm)

fun SimpleMatrix.colSum(): SimpleMatrix {
    val colSum = DoubleArray(numCols()) { j ->
        (0 until numRows()).fold(0.0) { acc, i -> acc + get(i, j) }
    }

    return SimpleMatrix(1, numCols(), true, colSum)
}

// TODO optimize to be in-place
fun List<SimpleMatrix>.avgNorm(): SimpleMatrix {
    val result = reduce { acc, simpleMatrix -> acc + simpleMatrix }
    result /= result.normF().toFloat()

    return result
}

fun SimpleMatrix.colNormalize(): SimpleMatrix = colSum().normalize()

fun SimpleMatrix.normalize(): SimpleMatrix = divide(normF())

/** Basic Operations */
fun FMatrixRMaj.scale(alpha: Float): FMatrixRMaj {
    CommonOps_FDRM.scale(alpha, this)
    return this
}

fun SimpleMatrix.iScale(alpha: Float): SimpleMatrix {
    CommonOps_FDRM.scale(alpha, fdrm)
    return this
}

operator fun SimpleMatrix.timesAssign(alpha: Float) {
    CommonOps_FDRM.scale(alpha, fdrm)
}

operator fun SimpleMatrix.divAssign(alpha: Float) {
    CommonOps_FDRM.divide(fdrm, alpha)
}

operator fun SimpleMatrix.minusAssign(other: SimpleMatrix) {
    fdrm -= other.fdrm
}

operator fun SimpleMatrix.plusAssign(other: SimpleMatrix) {
    if (this.type == MatrixType.FDRM) fdrm += other.fdrm
    else ddrm += other.ddrm
}


// def similarities_vectorized2(vector_data):
//    norms = np.linalg.norm(vector_data, axis=1)
//    combs = np.fromiter(combinations(range(vector_data.shape[0]),2), dtype='i,i')
//    similarities = (vector_data[combs['f0']]*vector_data[combs['f1']]).sum(axis=1)/norms[combs['f0']]/norms[combs['f1']]
//    return combs, similarities
// x·y / (||x|| × ||y||) = (x / ||x||) · (y / ||y||)
fun FMatrixRMaj.cosineDistanceOneToMany(other: FMatrixRMaj): FMatrixRMaj {
    val norms = this.normF()
    CommonOps_FDRM.divide(this, normP2())
    CommonOps_FDRM.divide(other, other.normP2())
    return times(other)
}