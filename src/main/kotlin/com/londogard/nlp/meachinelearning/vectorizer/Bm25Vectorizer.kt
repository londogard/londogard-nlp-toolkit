package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.datatypes.Percent
import com.londogard.nlp.meachinelearning.datatypes.PercentOrCount
import com.londogard.nlp.meachinelearning.transformers.Bm25Transformer
import com.londogard.nlp.meachinelearning.vectorizer.count.CountVectorizer
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

class Bm25Vectorizer<T: Number>(
    minDf: PercentOrCount = Percent(0.0),
    maxDf: PercentOrCount = Percent(1.0),
    ngramRange: IntRange = 1..1,
    k: Int,
    b: Float
    //val smoothing: Double = 0.4,
    //val vocab: Set<String>? = null
): Vectorizer<T, Float> {
    private val countVectorizer = CountVectorizer<T>(minDf, maxDf, ngramRange)
    private val bm25Transformer = Bm25Transformer(k, b)

    override fun fit(input: List<List<String>>) {
        val vectorized = countVectorizer.fitTransform(input)
        bm25Transformer.fit(vectorized)
    }

    override fun transform(input: List<List<String>>): MultiArray<Float, D2> {
        val vectorized = countVectorizer.transform(input)
        return bm25Transformer.transform(vectorized)
    }

    override fun fitTransform(input: List<List<String>>): MultiArray<Float, D2> {
        val vectorized = countVectorizer.fitTransform(input)
        return bm25Transformer.fitTransform(vectorized)
    }
}