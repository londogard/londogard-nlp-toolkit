package com.londogard.nlp.utils

import com.londogard.nlp.meachinelearning.dot
import org.ejml.data.FMatrixRMaj
import org.ejml.data.MatrixType.*
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.NormOps_FDRM
import org.ejml.kotlin.*
import org.ejml.simple.SimpleMatrix
import org.ejml.sparse.csc.CommonOps_DSCC
import org.ejml.sparse.csc.CommonOps_FSCC
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.operations.divAssign
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plusAssign
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Custom extensions for EJML simplification in Kotlin. Some optimized for speed.
 */

/** Basic Retrieval */
fun SimpleMatrix.getRow(index: Int): SimpleMatrix = extractVector(true, index)
fun SimpleMatrix.getCol(index: Int): SimpleMatrix = extractVector(false, index)
fun SimpleMatrix.getRows(rows: IntArray): SimpleMatrix =
    SimpleMatrix(CommonOps_FDRM.extract(fdrm, rows, numCols(), null))

fun SimpleMatrix.sumCols(): SimpleMatrix = when (this.type) {
    FDRM -> SimpleMatrix(CommonOps_FDRM.sumCols(fdrm, null))
    FSCC -> SimpleMatrix(CommonOps_FSCC.sumCols(fscc, null))
    DSCC -> SimpleMatrix(CommonOps_DSCC.sumCols(dscc, null))
    DDRM -> SimpleMatrix(CommonOps_DDRM.sumCols(ddrm, null))
    else -> throw Exception("Not supported yet")
}

fun SimpleMatrix.sumRows(): SimpleMatrix = when (this.type) {
    FDRM -> SimpleMatrix(CommonOps_FDRM.sumRows(fdrm, null))
    FSCC -> SimpleMatrix(CommonOps_FSCC.sumRows(fscc, null))
    DSCC -> SimpleMatrix(CommonOps_DSCC.sumRows(dscc, null))
    DDRM -> SimpleMatrix(CommonOps_DDRM.sumRows(ddrm, null))
    else -> throw Exception("Not supported yet")
}

/** Distance Algorithms */
fun SimpleMatrix.euclideanDistance(other: SimpleMatrix): Double = (this - other).normF()
fun SimpleMatrix.cosineDistance(other: SimpleMatrix): Double = this.dot(other) / (this.fastNormF() * other.fastNormF())

fun D2Array<Float>.cosineDistance(other: D2Array<Float>): D2Array<Float> {
    val dotProduct: D2Array<Float> = (this dot other) as D2Array<Float>
    dotProduct /= (mk.linalg.norm(this) * mk.linalg.norm(other)).toFloat()

    return dotProduct
}

fun D1Array<Float>.normP(p: Int): Float {
    var sum: Float = 0f
    for (x in data.indices) {
        sum += data[x].pow(p)
    }
    return sum.pow(1f / p)
}
fun D1Array<Float>.norm2(): Float {
    var sum: Float = 0f
    for (x in data.indices) {
        sum += data[x].pow(2)
    }
    return sqrt(sum)
}

fun D1Array<Float>.cosineDistance(other: D1Array<Float>): Float {
    return mk.linalg.dot(this, other) / (this.norm2() * other.norm2())
}

fun D2Array<Float>.euclideanDistance(other: D2Array<Float>) = mk.linalg.norm(this - other)
fun D1Array<Float>.euclideanDistance(other: D1Array<Float>) = (this - other)
    .norm2()

// var total = 0f
//
// var size: Int = a.getNumElements()
//
// for (i in 0 until size) {
//     val `val`: Float = a.get(i)
//     total += `val` * `val`
// }
//
// return java.lang.Math.sqrt(total.toDouble()) as kotlin.Float

/** Fast Normalizers: OBS prone to overflows/underflows */
fun SimpleMatrix.fastNormF(): Float = NormOps_FDRM.fastNormF(fdrm)

// TODO optimize to be in-place
fun List<SimpleMatrix>.avgNorm(): SimpleMatrix {
    val result = reduce { acc, simpleMatrix -> acc + simpleMatrix }
    result /= result.normF().toFloat()

    return result
}

fun List<D1Array<Float>>.avgNorm(): D1Array<Float> {
    val avgNormalized = this[0].clone()

    for (row in 1 until this.size) {
        avgNormalized.plusAssign(this[row])
    }

    avgNormalized /= avgNormalized.norm2()

    return avgNormalized
}

fun SimpleMatrix.colNormalize(): SimpleMatrix = sumCols().normalize()

fun SimpleMatrix.normalize(): SimpleMatrix = divide(normF())

/** Basic Operations */

fun SimpleMatrix.iScale(alpha: Float): SimpleMatrix = when (type) {
    FDRM -> CommonOps_FDRM.scale(alpha, fdrm)
    FSCC -> CommonOps_FSCC.scale(alpha, fscc, fscc)
    else -> throw UnsupportedOperationException("lol")
}.let { this }

fun createSparse(numRows: Int, numCols: Int, size: Int): SimpleMatrix {
    return SimpleMatrix(numRows, numCols, FSCC).apply { fscc.growMaxLength(size, false) }
}

operator fun SimpleMatrix.times(other: SimpleMatrix): SimpleMatrix = mult(other)

fun SimpleMatrix.map(op: (Float) -> Float): SimpleMatrix {
    return with(copy()) {
        when (type) {
            FDRM -> fdrm.data
                .forEachIndexed { i, d -> fdrm.data[i] = op(d) }
            FSCC -> fscc.nz_values
                .forEachIndexed { i, d -> fscc.nz_values[i] = op(d) }
            // DSCC -> dscc.nz_values
            //     .forEachIndexed { i, d -> dscc.nz_values[i] = op(d).toDouble() }
            // DDRM -> ddrm.data
            //     .forEachIndexed { i, d -> ddrm.data[i] = op(d).toDouble() }
            else -> throw Exception("Not supported yet")
        }
        this
    }
}

/**
 * Row, Col, Number
 */
fun SimpleMatrix.mapWithXY(op: (Int, Int, Double) -> Double): SimpleMatrix {
    with(copy()) {
        when (this.type) {
            FDRM, DDRM -> for (x in 0 until numRows()) for (y in 0 until numCols()) set(x, y, op(x, y, get(x, y)))
            FSCC -> fscc.col_idx.forEachIndexed { col, from ->
                for (i in from until fscc.col_idx[col])
                    fscc.nz_values[i] = op(fscc.nz_rows[i], col, fscc.nz_values[i].toDouble()).toFloat()
            }
            DSCC -> dscc.col_idx.forEachIndexed { col, from ->
                for (i in from until dscc.col_idx[col])
                    dscc.nz_values[i] = op(dscc.nz_rows[i], col, dscc.nz_values[i])
            }
            else -> throw Exception("Not supported yet")
        }
        return this
    }
}

fun SimpleMatrix.iMapWithCol(op: (Number, Int) -> Number): SimpleMatrix {
    when (this.type) {
        FDRM -> fdrm.data
            .forEachIndexed { i, d -> fdrm.data[i] = op(d, i % numCols()).toFloat() }
        FSCC -> fscc.col_idx.zip(fscc.col_idx.drop(1))
            .forEachIndexed { col, (from, to) ->
                for (i in from until to) fscc.nz_values[i] = op(fscc.nz_values[i], col).toFloat()
            }
        DSCC -> dscc.col_idx.zip(dscc.col_idx.drop(1))
            .forEachIndexed { col, (from, to) ->
                for (i in from until to) dscc.nz_values[i] = op(dscc.nz_values[i], col).toDouble()
            }
        DDRM -> ddrm.data
            .forEachIndexed { i, d -> ddrm.data[i] = op(d, i % numCols()).toDouble() }
        else -> throw Exception("Not supported yet")
    }
    return this
}

fun SimpleMatrix.iMap(op: (Float) -> Float): SimpleMatrix {
    when (this.type) {
        FDRM -> fdrm.data.forEachIndexed { i, d -> fdrm.data[i] = op(d) }
        FSCC -> fscc.nz_values.forEachIndexed { i, d -> fscc.nz_values[i] = op(d) }
        DSCC -> dscc.nz_values.forEachIndexed { i, d -> dscc.nz_values[i] = op(d.toFloat()).toDouble() }
        DDRM -> ddrm.data.forEachIndexed { i, d -> ddrm.data[i] = op(d.toFloat()).toDouble() }
        else -> throw Exception("Not supported yet")
    }
    return this
}

operator fun SimpleMatrix.timesAssign(alpha: Float) {
    CommonOps_FDRM.scale(alpha, fdrm)
}

operator fun SimpleMatrix.divAssign(alpha: Float) {
    when (this.type) {
        FDRM -> CommonOps_FDRM.divide(fdrm, alpha)
        FSCC -> CommonOps_FSCC.divide(fscc, alpha, fscc)
        DSCC -> CommonOps_DSCC.divide(dscc, 1 / alpha.toDouble(), dscc)
        DDRM -> CommonOps_DDRM.divide(ddrm, alpha.toDouble())
        else -> throw Exception("Not supported yet")
    }
}

operator fun SimpleMatrix.minusAssign(other: SimpleMatrix) {
    fdrm -= other.fdrm
}

operator fun SimpleMatrix.plusAssign(other: SimpleMatrix) = when (type to other.type) {
    FDRM to FDRM -> fdrm += other.fdrm
    DDRM to DDRM -> ddrm += other.ddrm
    else -> throw IllegalArgumentException("Cannot plusAssign other types")
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

fun SimpleMatrix.numElems(): Int = when (type) {
    FDRM, DDRM -> numElements
    FSCC -> fscc.nonZeroLength
    DSCC -> dscc.nonZeroLength
    else -> throw Exception("Not supported yet")
}