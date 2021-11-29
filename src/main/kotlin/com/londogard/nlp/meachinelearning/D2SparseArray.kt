package com.londogard.nlp.meachinelearning

import com.londogard.nlp.meachinelearning.datatypes.Coordinate
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.asSequence
import org.jetbrains.kotlinx.multik.ndarray.operations.count

typealias D2FloatArray = MultiArray<Float, D2>

fun MultiArray<Float, D2>.toSparse(): D2SparseArray {
    return if (this is D2SparseArray) this
    else {
        val coords = ArrayList<Coordinate<Float>>(count { it != 0f })

        for (col in 0 until shape[1])
            for (row in 0 until shape[0])
                if (get(row, col) != 0f) coords.add(Coordinate(row, col, get(row, col)))

        return D2SparseArray.simpleInit(coords, shape)
    }
}

fun MultiArray<Float, D2>.toDense(): D2Array<Float> {
    return if (this is D2SparseArray) {
        mk.ndarray(this.asSequence().toList(), shape[0], shape[1])
    } else this as D2Array<Float>
}

/** Compressed Column (CC) Sparse Matrix File Format */
class D2SparseArray(
    val rowIndices: IntArray,
    val colIndices: IntArray,
    override val data: MemoryView<Float>,
    override val shape: IntArray,
    override val base: MultiArray<Float, out Dimension>? = null
) : MultiArray<Float, D2> {
    override val consistent: Boolean = false
    override val dim: D2 = D2
    override val dtype: DataType = DataType.FloatDataType
    override val size: Int = shape.fold(1, Int::times)
    override val offset: Int = 0

    override val indices: IntRange
        get() = 0 until data.size
    override val multiIndices: MultiIndexProgression
        get() = throw UnsupportedOperationException("Not Supported by Sparse Matrix")
    override val strides: IntArray
        get() = throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun isEmpty(): Boolean = size == 0
    override fun isNotEmpty(): Boolean = !isEmpty()
    override fun isScalar(): Boolean = shape.isEmpty() || (shape.size == 1 && shape.first() == 1)

    override fun iterator(): Iterator<Float> = iterator {
        for (row in 0 until shape[0])
            for (col in 0 until shape[1]) {
                val index = indexMap[row to col]
                if (index != null) yield(data[index])
                else yield(0f)
            }
    }

    // [row, col]: index
    val indexMap: Map<Pair<Int, Int>, Int> by lazy {
        val mutableMap = HashMap<Pair<Int, Int>, Int>(data.size)

        for (col in 0 until colIndices.size - 1) {
            for (i in colIndices[col] until colIndices[col + 1]) {
                mutableMap[rowIndices[i] to col] = i
            }
        }

        mutableMap
    }

    operator fun get(row: Int, col: Int): Float {
        for (i in colIndices[col] until colIndices[col + 1])
            if (rowIndices[i] == row) return data[i]

        return 0f
    }

    override fun copy(): D2SparseArray =
        D2SparseArray(
            rowIndices.clone(),
            colIndices.clone(),
            MemoryViewFloatArray(data.getFloatArray().clone()),
            shape.clone(),
            base?.copy()
        )

    // 1,2 becomes 2,1 ... etc
    // e.g. rows becomes cols
    override fun transpose(vararg axes: Int): D2SparseArray {
        if (axes.isNotEmpty()) throw UnsupportedOperationException("Selecting Axes Not Supported")

        // TODO improve
        return D2SparseArray
            .simpleInit(indexMap.map { entry -> Coordinate(entry.key.second, entry.key.first, data[entry.value]) }.sortedBy { it.col }, shape.reversedArray())
    }

    override fun deepCopy(): MutableMultiArray<Float, D2> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun flatten(): MultiArray<Float, D1> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun reshape(dim1: Int): MutableMultiArray<Float, D1> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun reshape(dim1: Int, dim2: Int): MutableMultiArray<Float, D2> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun reshape(dim1: Int, dim2: Int, dim3: Int): MutableMultiArray<Float, D3> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun reshape(dim1: Int, dim2: Int, dim3: Int, dim4: Int): MutableMultiArray<Float, D4> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun reshape(dim1: Int, dim2: Int, dim3: Int, dim4: Int, vararg dims: Int): MutableMultiArray<Float, DN> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun squeeze(vararg axes: Int): MutableMultiArray<Float, DN> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun unsqueeze(vararg axes: Int): MutableMultiArray<Float, DN> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun cat(other: List<MultiArray<Float, D2>>, axis: Int): NDArray<Float, D2> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun cat(other: MultiArray<Float, D2>): NDArray<Float, D2> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

    override fun cat(other: MultiArray<Float, D2>, axis: Int): NDArray<Float, D2> =
        throw UnsupportedOperationException("Not Supported by Sparse Matrix")

        companion object {
        /** Requires initData to be sorted by column */
        fun simpleInit(sortedInitData: List<Coordinate<Float>>, shape: IntArray): D2SparseArray {
            val rowIndices = IntArray(sortedInitData.size) { i -> sortedInitData[i].row }
            val colIndices = IntArray(shape[1] + 1)
            val cols = sortedInitData.map(Coordinate<Float>::col)
            for (col in 1 until colIndices.size - 1) {
                colIndices[col] = (colIndices[col - 1] until cols.size).firstOrNull { i -> cols[i] == col } ?: sortedInitData.size
            }
            colIndices[colIndices.size - 1] = sortedInitData.size
            val data = MemoryViewFloatArray(FloatArray(sortedInitData.size) { i -> sortedInitData[i].count })
            return D2SparseArray(rowIndices, colIndices, data, shape)
        }
    }
}
fun MultiArray<Float, D2>.sum(byRow: Boolean): D1Array<Float> {
    return when {
        this is D2SparseArray && byRow -> rowIndices.foldIndexed(FloatArray(shape[0])) { dataIndex, acc, row ->
            acc[row] += data[dataIndex]
            acc
        }.let(mk::ndarray)
        this is D2SparseArray -> (0 until colIndices.size - 1).foldIndexed(FloatArray(shape[1])) { index, acc, col ->
            acc[index] = (colIndices[col] until colIndices[col + 1]).fold(0f) { sum, i -> sum + data[i] }
            acc
        }.let(mk::ndarray)
        else -> mk.math.sum(this, if (byRow) 0 else 1)
    }
}