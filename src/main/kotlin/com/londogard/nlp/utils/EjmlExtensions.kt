package com.londogard.nlp.utils

import org.ejml.data.FMatrixRMaj
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.NormOps_FDRM
import org.ejml.kotlin.*
import org.ejml.simple.SimpleMatrix


/** Basic Retrieval */
fun SimpleMatrix.getRow(index: Int): SimpleMatrix = extractVector(true, index)
fun FMatrixRMaj.getRow(row: Int, out: FMatrixRMaj? = null): FMatrixRMaj = CommonOps_FDRM.extractRow(this, row, out)

fun SimpleMatrix.getRows(rows: IntArray): SimpleMatrix =
    SimpleMatrix(CommonOps_FDRM.extract(fdrm, rows, numCols(), null))
fun FMatrixRMaj.getRows(rows: IntArray, out: FMatrixRMaj? = null): FMatrixRMaj =
    CommonOps_FDRM.extract(this, rows, this.getNumCols(), out)

fun FMatrixRMaj.sumCols(out: FMatrixRMaj? = null): FMatrixRMaj = CommonOps_FDRM.sumCols(this, out)
fun SimpleMatrix.sumCols(): SimpleMatrix = SimpleMatrix(CommonOps_FDRM.sumCols(fdrm, null))

/** Distance Algorithms */
fun SimpleMatrix.euclideanDistance(other: SimpleMatrix): Double = (this - other).normF()
fun SimpleMatrix.cosineDistance(other: SimpleMatrix): Double = this.dot(other) / (this.normF() * other.normF())
fun FMatrixRMaj.euclideanDistance(other: FMatrixRMaj): Float = (this - other).normF()
fun FMatrixRMaj.cosineDistance(other: FMatrixRMaj): Float = this.dot(other) / (this.normF() * other.normF())

/** Fast Normalizers: OBS prone to overflows/underflows */
fun FMatrixRMaj.fastNormF(): Float = NormOps_FDRM.fastNormF(this)

/** Basic Operations */
fun FMatrixRMaj.scale(alpha: Float): FMatrixRMaj {
    CommonOps_FDRM.scale(alpha, this)
    return this
}

fun FMatrixRMaj.iDiv(alpha: Float): FMatrixRMaj {
    CommonOps_FDRM.divide(this, alpha)
    return this
}

fun SimpleMatrix.iDiv(alpha: Float): SimpleMatrix {
    fdrm.iDiv(alpha)
    return this
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



fun main() {
    val a = FMatrixRMaj(floatArrayOf(1f,1f,1f))
    val b = FMatrixRMaj(floatArrayOf(3f,2f,2f))
    val c = FMatrixRMaj(arrayOf(floatArrayOf(3f,2f,2f)))
    println(a.cosineDistance(b))
    println(a.cosineDistanceOneToMany(c))
}

// Optimize with more in-place operations!
/** Basic Matrix Math */
fun FMatrixRMaj.dot(other: FMatrixRMaj): Float =
    when {
        numRows == 1 -> this.times(other.transpose()).get(0)
        numCols == 1 -> transpose().times(other).get(0)
        else -> throw AssertionError("Bad dimensions ${numRows}x$numCols vs ${other.numRows}x${other.numCols}")
    }