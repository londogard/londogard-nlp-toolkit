package com.londogard.nlp.meachinelearning.transformers

import com.londogard.nlp.meachinelearning.*
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.ln

// Based on SkLearns TfIdf variant. Missing norm option
class TfIdfTransformer : Transformer<Float, Float> {
    lateinit var idf: D1Array<Float>

    override fun fit(input: MultiArray<Float, D2>) {
        val sparseInput = input.toSparse()
        val numDocs = input.shape[0]

        val df = sparseInput
            .mapNonZero { 1f }

        idf = (df as D2SparseArray)
            .sum(byRow = false)
            .inplaceOp { totalDf -> ln(numDocs / (totalDf + 1)) + 1 } as D1Array<Float>
    }

    /** Input is a count matrix */
    override fun transform(input: MultiArray<Float, D2>): D2SparseArray {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }

        return input
            .toSparse()
            .mapIndexedNonZero { fl, _, col -> fl * idf[col] }
    }
}