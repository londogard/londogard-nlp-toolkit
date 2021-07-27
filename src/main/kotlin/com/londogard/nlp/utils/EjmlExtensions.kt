package com.londogard.nlp.utils

import org.ejml.data.FMatrixRMaj
import org.ejml.data.MatrixType
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.NormOps_FDRM
import org.ejml.kotlin.*
import org.ejml.simple.SimpleMatrix
import org.ejml.sparse.csc.CommonOps_DSCC
import org.ejml.sparse.csc.CommonOps_FSCC
import space.kscience.kmath.nd.Structure2D

/**
 * Custom extensions for EJML simplification in Kotlin. Some optimized for speed.
 */

/** Basic Retrieval */
fun SimpleMatrix.getRow(index: Int): SimpleMatrix = extractVector(true, index)
fun SimpleMatrix.getRows(rows: IntArray): SimpleMatrix =
    SimpleMatrix(CommonOps_FDRM.extract(fdrm, rows, numCols(), null))

fun SimpleMatrix.sumCols(): SimpleMatrix = when (this.type) {
    MatrixType.FDRM -> SimpleMatrix(CommonOps_FDRM.sumCols(fdrm, null))
    MatrixType.FSCC -> SimpleMatrix(CommonOps_FSCC.sumCols(fscc, null))
    MatrixType.DSCC -> SimpleMatrix(CommonOps_DSCC.sumCols(dscc, null))
    MatrixType.DDRM -> SimpleMatrix(CommonOps_DDRM.sumCols(ddrm, null))
    else -> throw Exception("Not supported yet")
}

fun SimpleMatrix.sumRows(): SimpleMatrix = when (this.type) {
    MatrixType.FDRM -> SimpleMatrix(CommonOps_FDRM.sumRows(fdrm, null))
    MatrixType.FSCC -> SimpleMatrix(CommonOps_FSCC.sumRows(fscc, null))
    MatrixType.DSCC -> SimpleMatrix(CommonOps_DSCC.sumRows(dscc, null))
    MatrixType.DDRM -> SimpleMatrix(CommonOps_DDRM.sumRows(ddrm, null))
    else -> throw Exception("Not supported yet")
}

/** Distance Algorithms */
fun SimpleMatrix.euclideanDistance(other: SimpleMatrix): Double = (this - other).normF()
fun SimpleMatrix.cosineDistance(other: SimpleMatrix): Double = this.dot(other) / (this.fastNormF() * other.fastNormF())

/** Fast Normalizers: OBS prone to overflows/underflows */
fun SimpleMatrix.fastNormF(): Float = NormOps_FDRM.fastNormF(fdrm)

// TODO optimize to be in-place
fun List<SimpleMatrix>.avgNorm(): SimpleMatrix {
    val result = reduce { acc, simpleMatrix -> acc + simpleMatrix }
    result /= result.normF().toFloat()

    return result
}

fun SimpleMatrix.colNormalize(): SimpleMatrix = sumCols().normalize()

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

fun createSparse(numRows: Int, numCols: Int, size: Int): SimpleMatrix {
    return SimpleMatrix(numRows, numCols, MatrixType.FSCC).apply { fscc.growMaxLength(size, false) }
}

fun SimpleMatrix.iMap(op: (Number) -> Number): SimpleMatrix {
    when (this.type) {
        MatrixType.FDRM -> fdrm.data
            .forEachIndexed { i, d -> fdrm.data[i] = op(d).toFloat() }
        MatrixType.FSCC -> fscc.nz_values
            .forEachIndexed { i, d -> fscc.nz_values[i] = op(d).toFloat() }
        MatrixType.DSCC -> dscc.nz_values
            .forEachIndexed { i, d -> dscc.nz_values[i] = op(d).toDouble() }
        MatrixType.DDRM -> ddrm.data
            .forEachIndexed { i, d -> ddrm.data[i] = op(d).toDouble() }
        else -> throw Exception("Not supported yet")
    }
    return this
}

operator fun SimpleMatrix.timesAssign(alpha: Float) {
    CommonOps_FDRM.scale(alpha, fdrm)
}

operator fun SimpleMatrix.divAssign(alpha: Float) {
    when (this.type) {
        MatrixType.FDRM -> CommonOps_FDRM.divide(fdrm, alpha)
        MatrixType.FSCC -> CommonOps_FSCC.divide(fscc, alpha, fscc)
        MatrixType.DSCC -> CommonOps_DSCC.divide(dscc, 1 / alpha.toDouble(), dscc)
        MatrixType.DDRM -> CommonOps_DDRM.divide(ddrm, alpha.toDouble())
        else -> throw Exception("Not supported yet")
    }
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