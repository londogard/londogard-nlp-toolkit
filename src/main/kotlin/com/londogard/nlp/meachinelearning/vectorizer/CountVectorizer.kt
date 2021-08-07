package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.SimpleMatrixUtils
import com.londogard.nlp.meachinelearning.inputs.Coordinate
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.utils.IterableExtensions.getNgramCountsPerDoc
import com.londogard.nlp.utils.IterableExtensions.mergeReduce
import org.ejml.simple.SimpleMatrix
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import java.util.*

// Pipeline Object
// Document Object
// HashingBagOfWords (collisions)

// TODO add actual T support
class CountVectorizer<T : Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE),
    val minDf: PercentOrCount = Percent(0.0),
    val maxDf: PercentOrCount = Percent(1.0),
    val ngramRange: IntRange = 1..1
) : BaseVectorizer<T, Float> {
    private lateinit var vectorization: Map<String, Int>

    init {
        if (ngramRange.first <= 0) throw IllegalArgumentException("ngramRange must be larger than 0")
    }

    override fun fit(input: List<Array<String>>) {
        val countMapDoc = input.getNgramCountsPerDoc(ngramRange)
        val countMap = countMapDoc.mergeReduce { a, b -> a + b }

        val totalCount = input.sumOf { it.size }

        // TODO update into array / bitset
        val df by lazy {
            countMap
                .mapValues { 0 }
                .mergeReduce(countMapDoc) { a, _ -> a + 1}
                .filter { (_, count) ->
                    minDf.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
                }
                .keys
        }

        vectorization = countMap
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .let { keys ->
                if (minDf.isEq(0) && maxDf.isEq(1)) keys
                else keys.filter(df::contains)
            }
            .withIndex()
            .associateBy({ it.value }, { it.index })
    }


    override fun transform(input: List<Array<String>>): SimpleMatrix {
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

        return SimpleMatrixUtils.sparseOf(input.size, vectorization.size, nonZeroElements)
    }

    override fun fitTransform(input: List<Array<String>>): SimpleMatrix {
        val countMapDoc = input.getNgramCountsPerDoc(ngramRange)
        val countMap = countMapDoc.mergeReduce { a, b -> a + b }
        val totalCount = input.sumOf { it.size }

        val df by lazy {
            countMap
                .mapValues { 0 }
                .mergeReduce(countMapDoc) { a, _ -> a + 1}
                .filter { (_, count) ->
                    minDf.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
                }
                .keys
        }

        vectorization = countMap
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .let { keys ->
                if (minDf.isEq(0) && maxDf.isEq(1)) keys
                else keys.filter(df::contains)
            }
            .withIndex()
            .associateBy({ it.value }, { it.index })

        val countMapFiltered = countMapDoc
            .flatMapIndexed { row, docCount ->
                docCount
                    .mapNotNull { (key, count) ->
                        vectorization[key]?.let { col -> Coordinate(row, col, count.toFloat()) }
                    }
            }

        return SimpleMatrixUtils.sparseOf(input.size, vectorization.size, countMapFiltered)
    }
}