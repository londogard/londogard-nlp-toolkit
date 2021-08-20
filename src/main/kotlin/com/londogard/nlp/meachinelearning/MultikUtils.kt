package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.datatypes.Coordinate
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.jvm.JvmLinAlg
import org.jetbrains.kotlinx.multik.ndarray.data.*

fun MultiArray<Float, D2>.mapNonZero(op: (Float) -> Float): MultiArray<Float, D2> {
    return clone().apply {
        val array = data.getFloatArray()
        for (i in indices) {
            array[i] = op(array[i])
        }
    }
}

fun D2SparseArray.mapIndexedNonZero(op: (Float, Int, Int) -> Float): D2SparseArray {
    return clone().apply {
        (0 until colIndices.size - 1).forEach { col ->
            (colIndices[col] until colIndices[col + 1]).forEach { i ->
                data[i] = op(data[i], rowIndices[i], col)
            }
        }
    }
}

fun <D : Dimension> MultiArray<Float, D>.inplaceOp(op: (Float) -> Float): MultiArray<Float, D> = apply {
    val internalData = data.getFloatArray()
    for (i in internalData.indices) {
        internalData[i] = op(internalData[i])
    }
}

infix fun MultiArray<Float, D2>.dot(other: MultiArray<Float, D2>): MultiArray<Float, D2> = when {
    this is D2SparseArray && other is D2SparseArray -> this dotSparse other
    this is D2SparseArray -> this dotDense other
    other is D2SparseArray -> this dotDense other
    else -> JvmLinAlg.dot(this, other)
}

infix fun MultiArray<Float, D2>.dotDense(other: D2SparseArray): D2Array<Float> {
    val result = mk.ndarray(FloatArray(shape[0] * other.shape[1]), shape[0], other.shape[1])
    other.indexMap
        .forEach { (rowCol, index) ->
            val (otherRow, otherCol) = rowCol


            (0 until shape[0]).forEach { thisRow ->
                result[thisRow, otherCol] += (other.data[index] * this[thisRow, otherRow])
            }
        }
    return result
}

infix fun D2SparseArray.dotDense(other: MultiArray<Float, D2>): MultiArray<Float, D2> {
    val result = mk.ndarray(FloatArray(shape[0] * other.shape[1]), shape[0], other.shape[1])
    indexMap
        .forEach { (rowCol, index) ->
            val (row, col) = rowCol
            // end index = this.row, other.col
            (0 until other.shape[1]).forEach { otherCol -> // multiply [_, col] by [col, _]
                result[row, otherCol] += (data[index] * other[col, otherCol])
            }
        }
    return result
}

// TODO validate if faster is possible
infix fun D2SparseArray.dotSparse(other: D2SparseArray): D2SparseArray {
    val data = other.colIndices
        .zip(other.colIndices.drop(1))
        .flatMapIndexed { otherCol, (otherFrom, otherTo) ->
            // 1,1 is multiplied by 1,1 .. 1,2 by 2,1 (row, col) * (col, row)

            // row x col --> [row,col]
            (0 until this.shape[0])
                .mapNotNull { thisRow ->
                    val sum = (otherFrom until otherTo)
                        .sumOf { otherDataIndex ->
                            // otherRow should multiply with our col on thisRow
                            val otherRow = other.rowIndices[otherDataIndex]
                            val thisIndex = this.indexMap[thisRow to otherRow] // [row, col] multiply with [col, row]

                            if (thisIndex != null)
                                (other.data[otherDataIndex] * this.data[thisIndex]).toDouble()
                            else 0.0
                        }

                    if (sum > 0) Coordinate(thisRow, otherCol, sum.toFloat())
                    else null
                }
        }

    return D2SparseArray.simpleInit(data, intArrayOf(shape[0], other.shape[1]))
}