package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.inputs.Coordinate
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.jvm.JvmLinAlg
import org.jetbrains.kotlinx.multik.ndarray.data.*

fun <D : Dimension> MultiArray<Float, D>.inplaceOp(op: (Float) -> Float): MultiArray<Float, D> = apply {
    val internalData = data.getFloatArray()
    for (i in internalData.indices) {
        internalData[i] = op(internalData[i])
    }
}

fun D2Array<Float>.sigmoid() = inplaceOp(::sigmoidFast)

infix fun MultiArray<Float, D2>.dot(other: MultiArray<Float, D2>): MultiArray<Float, D2> = when {
    this is D2SparseArray && other is D2SparseArray -> this dotSparse other
    this is D2SparseArray -> this dotDense other
    other is D2SparseArray -> this dot other.toDense()
    else -> JvmLinAlg.dot(this, other)
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

            // TODO("")
        }
    return result
}

infix fun D2SparseArray.dotSparse(other: D2SparseArray): D2SparseArray {
    // val colIndices = IntArray(other.shape[1] + 1)
    // val rowIndices = IntArray(other.data.size + data.size)
    // val nzValues = FloatArray(other.data.size + data.size)
    // var nzLength = 0

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