package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.transformers.TfIdfTransformer
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point

// TODO allow Double or Float :)
class TfIdfVectorizer<T: Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE)
): BaseVectorizer<T, Float> {
    private val countVectorizer = CountVectorizer<T>(minCount, maxCount)
    private val tfIdfTransformer = TfIdfTransformer()

    override fun fit(input: List<Point<T>>) {
        val vectorized = countVectorizer.fitTransform(input)
        tfIdfTransformer.fit(vectorized)
    }

    override fun transform(input: List<Point<T>>): Matrix<Float> {
        val vectorized = countVectorizer.transform(input)
        return tfIdfTransformer.transform(vectorized)
    }


    override fun fitTransform(input: List<Point<T>>): Matrix<Float> {
        val vectorized = countVectorizer.fitTransform(input)
        return tfIdfTransformer.fitTransform(vectorized)
    }
}

// TODO create TfIdfTransformer which builds TfIdfVectorizer when combined with CountVectorizer
// TODO sklearn simply use matrix multiplication and enforce same feature count (not allowing OOV)
/**
 *
 *
class TfIdfVectorizer<T : Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE),
    val minDf: PercentOrCount = Percent(0.0),
    val maxDf: PercentOrCount = Percent(1.0),
    val ngramRange: Pair<Int, Int> = Pair(1, 1),
    val smoothing: Double = 0.4,
    val vocab: Set<String>? = null
) : BaseVectorizer<T, Float> {
    // TODO Double?
    lateinit var idf: Map<T, Float>

    override fun fit(input: D2Array<T>) {
        val totalCount = input.size
        val count = input.groupingNDArrayBy { it }
            .eachCount()
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
        val termfreq = count.mapValues { (_, value) -> value.toFloat() / input.size }

        val mutableMap = count.mapValues { 0f }.toMutableMap()
        for (row in 0 until input.shape[0]) {
            input[row].toMutableSet().forEach { element ->
                mutableMap[element]?.let { currentCount -> mutableMap[element] = currentCount + 1 }
            }
        }
        val numDocs = input.shape[1]
        idf = mutableMap
            .filter { (_, count) -> minDf.isLesserThan(count, numDocs) && maxDf.isGreatherThan(count, numDocs) }
            .map { (key, value) -> key to value / numDocs }
            .toMap()
            .mapValues { (_, df) -> ln(1 / (1 + df)) + 1 }
    }

    override fun transform(input: D2Array<T>): D2Array<Float> {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }

        val transformed = mk.d2array(input.shape[0], idf.size) { 0f }
        for (row in 0 until input.shape[0]) {   // TODO swap to matrix-multiplication
            // TODO fix
//            input[row].groupingNDArrayBy { it }.eachCount().mapValues { count -> count / input }
            for (element in input[row]) {
                val index = vectorization[element]
                if (index != null) {
                    transformed[row, index] += 1
                }
            }
        }

        return transformed

        TODO("Not yet implemented")
    }
}*/