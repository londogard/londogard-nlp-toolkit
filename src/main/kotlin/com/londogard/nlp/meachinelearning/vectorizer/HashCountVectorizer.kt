package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.utils.IterableExtensions.ngrams
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set

// TODO
//  add min/maxDf
//  add stopwords

class HashCountVectorizer<T : Number>(
    val nFeatures: Int = 1_048_576,
    val ngramRange: IntRange = 1..1
) : Vectorizer<T, Float> {
    private lateinit var vectorization: Map<Int, Int>

    // Simple, fast but bad alternative
    private fun vectorize(token: String): Int = (token.hashCode().toUInt() % nFeatures.toUInt()).toInt()

    override fun fit(input: List<List<String>>) = Unit // no-op

    override fun transform(input: List<List<String>>): D2Array<Float> {
        val vectorized = mk.d2array(input.size, nFeatures) { 0f }

        ngramRange.forEach { n ->
            input.forEachIndexed { rowIndex, row ->
                row.ngrams(n).map(this::vectorize).forEach { key ->
                    vectorized[rowIndex, key] += 1f
                }
            }
        }

        return vectorized
    }

    override fun fitTransform(input: List<List<String>>): D2Array<Float> = transform(input)
}