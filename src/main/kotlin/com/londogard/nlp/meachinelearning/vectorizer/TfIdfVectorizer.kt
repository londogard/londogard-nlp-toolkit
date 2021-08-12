package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.transformers.TfIdfTransformer
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

// Based on SkLearns TfIdf variant. Missing norm option & smoothing
class TfIdfVectorizer<T: Number>(
    minDf: PercentOrCount = Percent(0.0),
    maxDf: PercentOrCount = Percent(1.0),
    ngramRange: IntRange = 1..1,
    //val smoothing: Double = 0.4,
    //val vocab: Set<String>? = null
): Vectorizer<T, Float> {
    private val countVectorizer = CountVectorizer<T>(minDf, maxDf, ngramRange)
    private val tfIdfTransformer = TfIdfTransformer()

    override fun fit(input: List<List<String>>) {
        val vectorized = countVectorizer.fitTransform(input)
        tfIdfTransformer.fit(vectorized)
    }

    override fun transform(input: List<List<String>>): MultiArray<Float, D2> {
        val vectorized = countVectorizer.transform(input)
        return tfIdfTransformer.transform(vectorized)
    }

    override fun fitTransform(input: List<List<String>>): MultiArray<Float, D2> {
        val vectorized = countVectorizer.fitTransform(input)
        return tfIdfTransformer.fitTransform(vectorized)
    }
}