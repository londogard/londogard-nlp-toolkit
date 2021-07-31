package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.transformers.TfIdfTransformer
import org.ejml.simple.SimpleMatrix

// Based on SkLearns TfIdf variant. Missing norm option & smoothing
class TfIdfVectorizer<T: Number>(
    minCount: PercentOrCount = Count(0),
    maxCount: PercentOrCount = Count(Int.MAX_VALUE),
    minDf: PercentOrCount = Percent(0.0),
    maxDf: PercentOrCount = Percent(1.0),
    ngramRange: IntRange = 1..1,
    //val smoothing: Double = 0.4,
    //val vocab: Set<String>? = null
): BaseVectorizer<T, Float> {
    private val countVectorizer = CountVectorizer<T>(minCount, maxCount, minDf, maxDf, ngramRange)
    private val tfIdfTransformer = TfIdfTransformer()

    override fun fit(input: List<Array<String>>) {
        val vectorized = countVectorizer.fitTransform(input)
        tfIdfTransformer.fit(vectorized)
    }

    override fun transform(input: List<Array<String>>): SimpleMatrix {
        val vectorized = countVectorizer.transform(input)
        return tfIdfTransformer.transform(vectorized)
    }

    override fun fitTransform(input: List<Array<String>>): SimpleMatrix {
        val vectorized = countVectorizer.fitTransform(input)
        return tfIdfTransformer.fitTransform(vectorized)
    }
}