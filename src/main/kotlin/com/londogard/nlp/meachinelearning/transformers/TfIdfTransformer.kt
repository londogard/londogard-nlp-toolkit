package com.londogard.nlp.meachinelearning.transformers

import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.utils.iMap
import com.londogard.nlp.utils.iMapWithCol
import com.londogard.nlp.utils.map
import com.londogard.nlp.utils.sumCols
import org.ejml.simple.SimpleMatrix
import kotlin.math.ln

// Based on SkLearns TfIdf variant. Missing norm option
class TfIdfTransformer : BaseTransformer<Float, Float> {
    lateinit var idf: SimpleMatrix

    override fun fit(input: SimpleMatrix) {
        input.convertToSparse()
        val numDocs = input.numRows()

        idf = input
            .map { n -> if (n.toInt() <= 0) 0 else 1 }
            .sumCols()
            .iMap { inNumDocs -> ln(numDocs / (inNumDocs.toFloat() + 1)) + 1 }
    }

    /** Input is a count matrix */
    override fun transform(input: SimpleMatrix): SimpleMatrix {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }
        input.convertToSparse()

        return input.iMapWithCol { number, i -> number.toFloat() * idf[i] }
    }
}