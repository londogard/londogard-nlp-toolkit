package com.londogard.nlp.meachinelearning.vectorizer

import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDManager
import ai.djl.ndarray.types.Shape
import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.multikext.MultikD2Wrapper
import com.londogard.nlp.meachinelearning.multikext.d2arraySparse
import com.londogard.nlp.meachinelearning.transformers.efficientSparseBuildMatrix
import com.londogard.nlp.utils.MapExtensions.mergeReduce
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.groupingNDArrayBy
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.structures.asIterable
import java.nio.IntBuffer


// Pipeline Object
// Document Object
// HashingBagOfWords (collisions)


class CountVectorizer<T : Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE),
) : BaseVectorizer<T, Float> {
    private lateinit var vectorization: Map<T, Int>

    fun fit(input: NDArray) {
        val totalCount = input.size().toInt()
        vectorization = input
            .toArray()
            .groupingBy { it }
            .eachCount()
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .mapIndexed { index, number -> number as T to index }
            .toMap()
    }

    fun transform(input: NDArray, manager: NDManager): NDArray {
        if (!::vectorization.isInitialized) {
            throw NotFitException("BagOfWordsVectorizer must be 'fit' before calling 'transform'!")
        }
        val countMap = input.toArray()
            .asIterable()
            .windowed(input.shape[0].toInt(), input.shape[0].toInt()) { window ->
                window.groupingBy { it }.eachCount().filterKeys { key -> key in vectorization }
                    .let { filteredRowCount -> filteredRowCount.mapKeys { (key, _) -> vectorization[key] ?: -1 } }
            }
            .map {
                it.toList().sortedBy { it.first }
            } // https://en.wikipedia.org/wiki/Sparse_matrix#Coordinate_list_(COO)
        val values = countMap.flatMap { it.map { it.second } }
        val indices = countMap
            .flatMapIndexed { index: Int, list: List<Pair<Int, Int>> -> list.map { (col, _) -> index.toLong() to col.toLong() } }
            .unzip()

        return manager.createCoo(
            IntBuffer.wrap(values.toIntArray()),
            arrayOf(indices.first.toLongArray(), indices.second.toLongArray()),
            Shape(input.shape[0], vectorization.size.toLong())
        )
    }

    fun fit(input: D2Array<T>) {
        val totalCount = input.size
        vectorization = input
            .groupingNDArrayBy { it }
            .eachCount().filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .mapIndexed { index, t -> t to index }
            .toMap()
    }

    fun transform(input: D2Array<T>): MultikD2Wrapper<Int> {
        if (!::vectorization.isInitialized) {
            throw NotFitException("BagOfWordsVectorizer must be 'fit' before calling 'transform'!")
        }

        val (indices, counts) = (0 until input.shape[0]).fold(emptyList<List<Pair<Long, Int>>>()) { acc, row ->
            val a = input[row].groupingNDArrayBy { it }.eachCount()
                .filterKeys { key -> key in vectorization }
                .mapNotNull { (key, value) -> vectorization[key]?.let { it to value } }
                .sortedBy { it.first }
                .map { (index, count) -> index.toLong() * row to count }

            acc.plus(element = a)
        }.flatten().unzip()

        return mk.d2arraySparse(input.shape[0], vectorization.size, indices.toLongArray()) { i -> counts[i] }
    }

    // TODO perhaps List<Vector> as input?
    override fun fit(input: Matrix<T>) {
        val countMap = input.rows
            .map { it.asIterable().groupingBy { it }.eachCount() }

        val totalCount = input.rowNum * input.colNum
        vectorization = mutableMapOf<T, Int>()
            .mergeReduce(countMap) { a, b -> a + b }
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .mapIndexed { index, t -> t to index }
            .toMap()
    }


    override fun transform(input: Structure2D<T>): Structure2D<Float> {
        if (!::vectorization.isInitialized) {
            throw NotFitException("BagOfWordsVectorizer must be 'fit' before calling 'transform'!")
        }

        val countMap = input.rows
            .map { it.asIterable().groupingBy { it }.eachCount().filterKeys { key -> key in vectorization } }
            .map { count -> count.mapKeys { (key, _) -> vectorization[key] ?: -1 } }
        val totalElems = countMap.sumOf { it.size }

        return efficientSparseBuildMatrix(input.rowNum, vectorization.size, totalElems) { i, j ->
            (countMap[i][j] ?: 0).toFloat()
        }
    }
}