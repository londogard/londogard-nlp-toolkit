package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.*

fun computeStrides(shape: IntArray): IntArray = shape.clone().apply {
    this[this.lastIndex] = 1
    for (i in this.lastIndex - 1 downTo 0) {
        this[i] = this[i + 1] * shape[i + 1]
    }
}

public class SparseNDArray<T : Number, D : Dimension> constructor(
    data: ImmutableMemoryView<T>,
    public override val offset: Int = 0,
    public override val shape: IntArray,
    public override val strides: IntArray = computeStrides(shape),
    public override val dtype: DataType,
    public override val dim: D,
    val numElements: Int,
    val nonZeroIndices: IntArray
) : MutableMultiArray<T, D>{
    init {
        check(shape.isNotEmpty()) { "Shape can't be empty."}
    }

    public override val data: MemoryView<T> = data as MemoryView<T>

    public override val size: Int get() = shape.fold(1, Int::times)

    public override val consistent: Boolean
        get() {
            return offset == 0 && size == data.size && strides.contentEquals(computeStrides(shape))
        }

    override val indices: IntRange
        get() {
            return 0 until size
        }

    override val multiIndices: MultiIndexProgression get() = IntArray(dim.d)..IntArray(dim.d) { shape[it] - 1 }

    override fun isScalar(): Boolean = shape.isEmpty() || (shape.size == 1 && shape.first() == 1)

    public override fun isEmpty(): Boolean = size == 0

    public override fun isNotEmpty(): Boolean = !isEmpty()

    public override operator fun iterator(): Iterator<T> =
        if (consistent) this.data.iterator() else NDArrayIterator(data, offset, strides, shape)

    public inline fun <reified E : Number> asType(): NDArray<E, D> {
        val dataType = DataType.of(E::class)
        return this.asType(dataType)
    }

    public fun <E : Number> asType(dataType: DataType): NDArray<E, D> {
        val newData = initMemoryView<E>(this.data.size, dataType) { this.data[it] as E }
        return NDArray<E, D>(newData, this.offset, this.shape, this.strides, dataType, this.dim)
    }

    override fun clone(): NDArray<T, D> =
        NDArray(this.data.copyOf(), this.offset, this.shape.copyOf(), this.strides.copyOf(), this.dtype, this.dim)

    override fun deepCopy(): NDArray<T, D> {
        val data = initMemoryView<T>(this.size, this.dtype)
        var index = 0
        for (el in this)
            data[index++] = el
        return NDArray<T, D>(data, 0, this.shape.copyOf(), dtype = this.dtype, dim = this.dim)
    }

    override fun flatten(): MultiArray<T, D1> {
        val data = if (consistent) {
            data.copyOf()
        } else {
            val tmpData = initMemoryView<T>(size, dtype)
            var index = 0
            for (el in this) tmpData[index++] = el
            tmpData
        }
        return D1Array(data, 0, intArrayOf(size), dtype = this.dtype, dim = D1)
    }

    // TODO(strides? : view.reshape().reshape()?)
    override fun reshape(dim1: Int): D1Array<T> {
        // todo negative shape?
//        requirePositiveShape(dim1)
        require(dim1 == size) { "Cannot reshape array of size $size into a new shape ($dim1)" }

        return if (this.dim.d == 1 && this.shape.first() == dim1) {
            this as D1Array<T>
        } else {
            D1Array<T>(this.data, this.offset, intArrayOf(dim1), dtype = this.dtype, dim = D1)
        }
    }

    override fun reshape(dim1: Int, dim2: Int): D2Array<T> {
        val newShape = intArrayOf(dim1, dim2)
//        newShape.forEach { requirePositiveShape(it) }
        require(dim1 * dim2 == size) { "Cannot reshape array of size $size into a new shape ($dim1, $dim2)" }

        return if (this.shape.contentEquals(newShape)) {
            this as D2Array<T>
        } else {
            D2Array<T>(this.data, this.offset, newShape, dtype = this.dtype, dim = D2)
        }
    }

    override fun reshape(dim1: Int, dim2: Int, dim3: Int): D3Array<T> {
        val newShape = intArrayOf(dim1, dim2, dim3)
//        newShape.forEach { requirePositiveShape(it) }
        require(dim1 * dim2 * dim3 == size) { "Cannot reshape array of size $size into a new shape ($dim1, $dim2, $dim3)" }

        return if (this.shape.contentEquals(newShape)) {
            this as D3Array<T>
        } else {
            D3Array<T>(this.data, this.offset, newShape, dtype = this.dtype, dim = D3)
        }
    }

    override fun reshape(dim1: Int, dim2: Int, dim3: Int, dim4: Int): D4Array<T> {
        val newShape = intArrayOf(dim1, dim2, dim3, dim4)
//        newShape.forEach { requirePositiveShape(it) }
        require(dim1 * dim2 * dim3 * dim4 == size) { "Cannot reshape array of size $size into a new shape ($dim1, $dim2, $dim3, $dim4)" }

        return if (this.shape.contentEquals(newShape)) {
            this as D4Array<T>
        } else {
            D4Array<T>(this.data, this.offset, newShape, dtype = this.dtype, dim = D4)
        }
    }

    override fun reshape(dim1: Int, dim2: Int, dim3: Int, dim4: Int, vararg dims: Int): NDArray<T, DN> {
        val newShape = intArrayOf(dim1, dim2, dim3, dim4) + dims
//        newShape.forEach { requirePositiveShape(it) }
        require(newShape.fold(1, Int::times) == size) {
            "Cannot reshape array of size $size into a new shape ${newShape.joinToString(prefix = "(", postfix = ")")}"
        }

        return if (this.shape.contentEquals(newShape)) {
            this as NDArray<T, DN>
        } else {
            NDArray<T, DN>(this.data, this.offset, newShape, dtype = this.dtype, dim = DN(newShape.size))
        }
    }

    override fun transpose(vararg axes: Int): NDArray<T, D> {
        require(axes.isEmpty() || axes.size == dim.d) { "All dimensions must be indicated." }
        for (axis in axes) require(axis in 0 until dim.d) { "Dimension must be from 0 to ${dim.d}." }
        require(axes.toSet().size == axes.size) { "The specified dimensions must be unique." }
        val newShape: IntArray
        val newStrides: IntArray
        if (axes.isEmpty()) {
            newShape = this.shape.reversedArray()
            newStrides = this.strides.reversedArray()
        } else {
            newShape = IntArray(this.shape.size)
            newStrides = IntArray(this.strides.size)
            for ((i, axis) in axes.withIndex()) {
                newShape[i] = this.shape[axis]
                newStrides[i] = this.strides[axis]
            }
        }
        return NDArray(this.data, this.offset, newShape, newStrides, this.dtype, this.dim)
    }

    override fun squeeze(vararg axes: Int): NDArray<T, DN> {
        val cutAxes = if (axes.isEmpty()) {
            shape.withIndex().filter { it.value == 1 }.map { it.index }
        } else {
            require(axes.all { shape[it] == 1 }) { "Cannot select an axis to squeeze out which has size not equal to one." }
            axes.toList()
        }
        val newShape = this.shape.sliceArray(this.shape.indices - cutAxes)
        return NDArray<T, DN>(this.data, this.offset, newShape, dtype = this.dtype, dim = DN(newShape.size))
    }

    override fun unsqueeze(vararg axes: Int): NDArray<T, DN> {
        val newShape = shape.toMutableList()
        for (axis in axes.sorted()) {
            newShape.add(axis, 1)
        }
        return NDArray<T, DN>(
            this.data,
            this.offset,
            newShape.toIntArray(),
            dtype = this.dtype,
            dim = DN(newShape.size)
        )
    }

    override fun cat(other: MultiArray<T, D>, axis: Int): NDArray<T, DN> {
        require(
            this.shape.withIndex()
                .all { it.index == axis || it.value == other.shape[it.index] }) { "All dimensions of input arrays for the concatenation axis must match exactly." }

        val newShape = this.shape.copyOf()
        newShape[axis] = this.shape[axis] + other.shape[axis]

        val thisIt = this.iterator()
        val otherIt = other.iterator()
        var index = 0
        val ret = NDArray<T, DN>(
            initMemoryView(newShape.fold(1, Int::times), this.dtype),
            0,
            newShape,
            dtype = this.dtype,
            dim = DN(newShape.size)
        )
        while (thisIt.hasNext())
            ret.data[index++] = thisIt.next()
        while (otherIt.hasNext())
            ret.data[index++] = otherIt.next()
        return ret
    }

    //todo extensions
    public fun asD1Array(): D1Array<T> {
        if (this.dim.d == 1) return this as D1Array<T>
        else throw ClassCastException("Cannot cast NDArray of dimension ${this.dim.d} to NDArray of dimension 1.")
    }

    //todo
    public fun asD2Array(): D2Array<T> {
        if (this.dim.d == 2) return this as D2Array<T>
        else throw ClassCastException("Cannot cast NDArray of dimension ${this.dim.d} to NDArray of dimension 2.")
    }

    public fun asD3Array(): D3Array<T> {
        if (this.dim.d == 3) return this as D3Array<T>
        else throw ClassCastException("Cannot cast NDArray of dimension ${this.dim.d} to NDArray of dimension 3.")
    }

    public fun asD4Array(): D4Array<T> {
        if (this.dim.d == 4) return this as D4Array<T>
        else throw ClassCastException("Cannot cast NDArray of dimension ${this.dim.d} to NDArray of dimension 4.")
    }

    public fun asDNArray(): NDArray<T, DN> {
        if (this.dim.d == -1) throw Exception("Array dimension is undefined")
        if (this.dim.d > 4) return this as NDArray<T, DN>

        return NDArray(this.data, this.offset, this.shape, this.strides, this.dtype, DN(this.dim.d))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NDArray<*, *>

        if (size != other.size) return false
        if (!shape.contentEquals(other.shape)) return false
        if (dtype != other.dtype) return false
        if (dim != other.dim) return false

        val thIt = this.iterator()
        val othIt = other.iterator()
        while (thIt.hasNext() && othIt.hasNext()) {
            if (thIt.next() != othIt.next())
                return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 1
        for (el in this) {
            result = 31 * result + el.hashCode()
        }
        return result
    }

}