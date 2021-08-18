package com.londogard.nlp.meachinelearning.vectorizer.count

import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.inputs.Coordinate
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.vectorizer.Vectorizer
import com.londogard.nlp.utils.IterableExtensions.getNgramCountsPerDoc
import com.londogard.nlp.utils.IterableExtensions.identityCount
import com.londogard.nlp.utils.IterableExtensions.ngrams
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.set

/** DenseCountVectorizer uses dense data.
 * Dense data is faster at operating, but because the data is usually so sparse a sparse structure ends up faster.
 * Only use this if you're sure your data is very dense!
 */
class DenseCountVectorizer<T : Number>(
    val minDf: PercentOrCount = Percent(0.0),
    val maxDf: PercentOrCount = Percent(1.0),
    val ngramRange: IntRange = 1..1
) : Vectorizer<T, Float> {
    private lateinit var vectorization: Map<String, Int>

    init {
        if (ngramRange.first <= 0) throw IllegalArgumentException("ngramRange must be larger than 0")
    }

    /** Does a fitTransform */
    override fun fit(input: List<List<String>>) {
        fitTransform(input)
    }

    override fun transform(input: List<List<String>>): D2Array<Float> {
        if (!::vectorization.isInitialized) {
            throw NotFitException("CountVectorizer must be 'fit' before calling 'transform'!")
        }
        if (input.size.toLong() * vectorization.size > Int.MAX_VALUE) {
            throw UnsupportedOperationException("Data is too large (${input.size.toLong() * vectorization.size}). Requires Hashing or Sparse.")
        }

        val nonZeroElements = input
            .getNgramCountsPerDoc(ngramRange)
            .flatMapIndexed { row, docCount ->
                docCount
                    .mapNotNull { (key, count) ->
                        vectorization[key]?.let { col -> Coordinate(row, col, count.toFloat()) }
                    }
            }

        val vectorized = mk.ndarray(FloatArray(input.size * vectorization.size), input.size, vectorization.size)
        nonZeroElements.forEach { coordinate -> vectorized[coordinate.row, coordinate.col] = coordinate.count }

        return vectorized
    }

    override fun fitTransform(input: List<List<String>>): D2Array<Float> {
        val vocab = mutableMapOf<String, Int>()
        val vectorized = input
            .asSequence()
            .map { tokens ->
                ngramRange
                    .flatMap { tokens.ngrams(it) }
                    .map { vocab.getOrPut(it) { vocab.size } }
                    .identityCount()
            }
            .toList()

        val totalCount = input.sumOf { it.size }

        val toRemove by lazy {
            val dfArray = IntArray(vocab.size)
            vectorized.forEach { rowCount -> rowCount.keys.forEach { i -> dfArray[i] += 1 } }
            dfArray.forEachIndexed { index, count ->
                if (minDf.isLesserThan(count, totalCount) && maxDf.isGreatherThan(count, totalCount)) Unit
                else dfArray[index] = -1
            }
            dfArray
        }

        val (vec, remap) = vocab
            .toList()
            .filterNot { (_, index) -> toRemove[index] == -1 }
            .mapIndexed { index, (key, oldIndex) -> (key to index) to (oldIndex to index) }
            .unzip()
        vectorization = vec.toMap()

        if (input.size.toLong() * vectorization.size > Int.MAX_VALUE) {
            throw UnsupportedOperationException("Data is too large (${(input.size.toLong() * vectorization.size) / 1e6} million elements). Requires Hashing or Sparse.")
        }

        val remapMap = remap.toMap()
        val remappingArray = IntArray(vocab.size) { i -> remapMap.getOrDefault(i, -1) }

        val countMapFiltered = vectorized
            .flatMapIndexed { row, docCount ->
                docCount
                    .filter { (key, _) -> remappingArray[key] != -1 }
                    .map { (key, count) -> Coordinate(row, remappingArray[key], count.toFloat()) }
            }

        val remappedVectorized = mk.ndarray(FloatArray(input.size * vectorization.size), input.size, vectorization.size)
        countMapFiltered.forEach { coordinate -> remappedVectorized[coordinate.row, coordinate.col] = coordinate.count }

        return remappedVectorized
    }
}