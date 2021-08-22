package com.londogard.nlp.meachinelearning.transformers

import com.londogard.nlp.meachinelearning.*
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import kotlin.math.ln

// Implementation based on https://kmwllc.com/index.php/2020/03/20/understanding-tf-idf-and-bm-25/
class Bm25Transformer(val k: Int, val b: Float) : Transformer<Float, Float> {
    lateinit var idf: D1Array<Float>

    override fun fit(input: MultiArray<Float, D2>) {
        val numDocs = input.shape[1]
        val sparseInput = input.toSparse()
        val df = sparseInput.mapNonZero { 1f }

        idf = (df as D2SparseArray)
            .sum(byRow = false)
            // IDF = log (1 + (N - DF + .5)/(DF + .5)), based on Lucene
            .inplaceOp { totalDf -> ln(1 + (numDocs - totalDf - 0.5f) / (totalDf + 0.5f)) }
            .asDNArray().asD1Array()
    }

    /** Input is a count matrix */
    override fun transform(input: MultiArray<Float, D2>): D2SparseArray {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }
        val sparseInput = input.toSparse()

        val docLengths = sparseInput.sum(byRow = true)
        val avgDocLength = docLengths.sum() / input.shape[0]

        // TF: freq / (freq + k1 * (1 — b + b * dl / avgdl))
        return sparseInput
            .mapIndexedNonZero { value, row, col ->
                val tf = (value / (value + (k * (1 - b + (b * docLengths[row] / avgDocLength)))))
                tf * idf[col]
            }
    }
}