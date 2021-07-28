package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.KmathUtils.efficientSparseBuildMatrix
import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.inputs.Coordinate
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.utils.MapExtensions.mergeReduce
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.asIterable


// Pipeline Object
// Document Object
// HashingBagOfWords (collisions)

class CountVectorizer<T : Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE),
) : BaseVectorizer<T, Float> {
    private lateinit var vectorization: Map<T, Int>

    // TODO perhaps List<Vector> as input?
    override fun fit(input: List<Point<T>>) {
        val countMap = input
            .map { row -> row.asIterable().groupingBy { it }.eachCount() }

        val totalCount = input.sumOf { it.size }
        vectorization = mutableMapOf<T, Int>()
            .mergeReduce(countMap) { a, b -> a + b }
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .mapIndexed { index, t -> t to index }
            .toMap()
    }


    override fun transform(input: List<Point<T>>): Matrix<Float> {
        if (!::vectorization.isInitialized) {
            throw NotFitException("CountVectorizer must be 'fit' before calling 'transform'!")
        }

        val countMap = input
            .flatMapIndexed { row, buffer ->
                buffer.asIterable()
                    .groupingBy { it }
                    .eachCount()
                    .mapNotNull { (key, count) -> vectorization[key]?.let { col -> Coordinate(row, col, count.toFloat()) } }
            }
        val totalElems = countMap.size

        return efficientSparseBuildMatrix(input.size, vectorization.size, totalElems, countMap)
    }
}

/**
 * MultiK && NDArray (DJL) approaches
fun fit(input: NDArray) {
    val totalCount = input.size().toInt()

    vectorization = input
        .toIntArray().asIterable()
        .groupingBy { it }
        .eachCount()
        .filter { (_, count) ->
            minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
        }
        .asIterable()
        .mapIndexed { index, (number, _) -> number as T to index }
        .toMap()
}

fun transform(input: NDArray, manager: NDManager): NDArray {
    if (!::vectorization.isInitialized) {
        throw NotFitException("BagOfWordsVectorizer must be 'fit' before calling 'transform'!")
    }
    val countMap = input.toIntArray()
        .asIterable()
        .windowed(input.shape[1].toInt(), input.shape[1].toInt()) { window ->
            window.groupingBy { it }.eachCount()
                .let { filteredRowCount -> filteredRowCount
                    .mapNotNull { (key, count) -> vectorization[key as T]
                        ?.let { index -> index to count } }
                }.sortedBy { it.first } // https://en.wikipedia.org/wiki/Sparse_matrix#Coordinate_list_(COO)
        }
    val values = countMap.flatMap { it.map { it.second } }
    val indices = countMap
        .flatMapIndexed { index: Int, list: List<Pair<Int, Int>> -> list.map { (col, _) -> index.toLong() to col.toLong() } }
        .unzip()

    return manager.createCoo(
        IntBuffer.wrap(values.toIntArray()),
        arrayOf(indices.first.toLongArray(), indices.second.toLongArray()),
        Shape(input.shape[0], vectorization.size.toLong() + 1)
    )
}

fun fit(input: D2Array<T>) {
    val totalCount = input.size
    vectorization = input
        .groupingNDArrayBy { it }
        .eachCount()
        .filter { (_, count) ->
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

    val (indices, counts) = (0 until input.shape[0]).flatMap { row ->
        input[row]
            .groupingNDArrayBy { it }
            .eachCount()
            .mapNotNull { (key, value) -> vectorization[key]?.let { it to value } }
            .map { (index, count) -> index.toLong() * (row + 1) to count }
    }.unzip()

    return mk.d2arraySparse(input.shape[0], vectorization.size, indices.toLongArray()) { i -> counts[i] }
}
*/