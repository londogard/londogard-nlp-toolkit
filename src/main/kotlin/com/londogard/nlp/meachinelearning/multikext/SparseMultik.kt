package com.londogard.nlp.meachinelearning.multikext

import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.*
import java.util.*

fun computeStrides(shape: IntArray): IntArray = shape.clone().apply {
    this[this.lastIndex] = 1
    for (i in this.lastIndex - 1 downTo 0) {
        this[i] = this[i + 1] * shape[i + 1]
    }
}

interface MultikD2Wrapper<T: Number> {
    val data: D2Array<T>
    val isSparse: Boolean
}

class D2ArrayWrap<T: Number>(override val data: D2Array<T>): MultikD2Wrapper<T> {
    override val isSparse: Boolean = false
}

class D2SparseArray<T: Number>(val indices: LongArray, override val data: D2Array<T>): MultikD2Wrapper<T> {
    private val bitmap = BitSet.valueOf(indices)
    private val indiceMap = indices.mapIndexed { index, i -> i to index }.toMap()
    override val isSparse: Boolean = true

    @JvmName("get2")
    operator fun MultiArray<Int, D2>.get(ind1: Int, ind2: Int): Int {
        checkBounds(ind1 in 0 until this.shape[0], ind1, 0, this.shape[0])
        checkBounds(ind2 in 0 until this.shape[1], ind2, 1, this.shape[1])

        val index = ind1 * ind2
        return if (bitmap.get(index)) data[indiceMap[index.toLong()]!!] else 0
    }
    @JvmName("get2")
    operator fun MultiArray<Float, D2>.get(ind1: Int, ind2: Int): Float {
        checkBounds(ind1 in 0 until this.shape[0], ind1, 0, this.shape[0])
        checkBounds(ind2 in 0 until this.shape[1], ind2, 1, this.shape[1])

        val index = ind1 * ind2
        return if (bitmap.get(index)) data[indiceMap[index.toLong()]!!] else 0f
    }
    @JvmName("get2")
    operator fun MultiArray<Double, D2>.get(ind1: Int, ind2: Int): Double {
        checkBounds(ind1 in 0 until this.shape[0], ind1, 0, this.shape[0])
        checkBounds(ind2 in 0 until this.shape[1], ind2, 1, this.shape[1])

        val index = ind1 * ind2
        return if (bitmap.get(index)) data[indiceMap[index.toLong()]!!] else 0.0
    }
}

public inline fun <reified T : Number> Multik.d2arraySparse(sizeD1: Int, sizeD2: Int, indices: LongArray, noinline init: (Int) -> T): D2SparseArray<T> =
    D2SparseArray(indices, mk.d2array(sizeD1, sizeD2, init))