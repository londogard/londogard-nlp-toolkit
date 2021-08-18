package com.londogard.nlp.meachinelearning.vectorizer.count

import com.londogard.nlp.meachinelearning.D2SparseArray
import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.inputs.Coordinate
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.vectorizer.Vectorizer
import com.londogard.nlp.utils.IterableExtensions.getNgramCountsPerDoc
import com.londogard.nlp.utils.IterableExtensions.identityCount
import com.londogard.nlp.utils.IterableExtensions.ngrams

// TODO add actual T support
class CountVectorizer<T : Number>(
    val minDf: PercentOrCount = Percent(0.0),
    val maxDf: PercentOrCount = Percent(1.0),
    val ngramRange: IntRange = 1..1
) : Vectorizer<T, Float> {
    lateinit var vectorization: Map<String, Int>

    init {
        if (ngramRange.first <= 0) throw IllegalArgumentException("ngramRange must be larger than 0")
    }

    /** Does a fitTransform */
    override fun fit(input: List<List<String>>) {
        fitTransform(input)
    }

    override fun transform(input: List<List<String>>): D2SparseArray {
        if (!::vectorization.isInitialized) {
            throw NotFitException("CountVectorizer must be 'fit' before calling 'transform'!")
        }

        val nonZeroElements = input
            .getNgramCountsPerDoc(ngramRange)
            .flatMapIndexed { row, docCount ->
                docCount
                    .mapNotNull { (key, count) ->
                        vectorization[key]?.let { col -> Coordinate(row, col, count.toFloat()) }
                    }
            }
            .sortedBy { it.col }

        return D2SparseArray.simpleInit(nonZeroElements, intArrayOf(input.size, vectorization.size))
    }

    override fun fitTransform(input: List<List<String>>): D2SparseArray {
        val vocab = mutableMapOf<String, Int>()
        val vectorized = input
            .map { tokens ->
                ngramRange
                    .flatMap { tokens.ngrams(it) }
                    .map { vocab.getOrPut(it) { vocab.size } }
                    .identityCount()
            }
        val totalCount = input.sumOf { it.size }

        val toRemove by lazy {
            val dfArray = IntArray(vocab.size)
            vectorized.forEach { rowCount -> rowCount.keys.forEach { i -> dfArray[i] += 1 } }
            dfArray.forEachIndexed { index, count ->
                if (!(minDf.isLesserThan(count, totalCount) && maxDf.isGreatherThan(count, totalCount)))
                    dfArray[index] = -1
            }
            dfArray
        }

        val (vec, remap) = vocab
            .asIterable()
            .filterNot { toRemove[it.value] == -1 }
            .mapIndexed { index, (key, oldIndex) -> (key to index) to (oldIndex to index) }
            .unzip()
        vectorization = vec.toMap()
        val remappingArray = IntArray(vocab.size) { -1 }
        remap.forEach { (from, to) -> remappingArray[from] = to }

        val countMapFiltered = vectorized
            .flatMapIndexed { row, docCount ->
                docCount
                    .asIterable()
                    .filter { (key, _) -> remappingArray[key] != -1 }
                    .map { (key, count) -> Coordinate(row, remappingArray[key], count.toFloat()) }
            }
            .sortedBy { it.col }

        return D2SparseArray.simpleInit(
            countMapFiltered,
            intArrayOf(input.size, vectorization.size)
        )
    }
}