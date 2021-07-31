package com.londogard.nlp.meachinelearning.transformers

import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.utils.*
import org.ejml.simple.SimpleMatrix
import kotlin.math.ln

// Implementation based on https://kmwllc.com/index.php/2020/03/20/understanding-tf-idf-and-bm-25/
class Bm25Transformer(val k: Int, val b: Double) : BaseTransformer<Float, Float> {
    lateinit var idf: SimpleMatrix

    override fun fit(input: SimpleMatrix) {
        input.convertToSparse()
        val numDocs = input.numRows()

        idf = input
            .map { n -> if (n.toInt() <= 0) 0 else 1 }
            .sumCols()
            // IDF = log (1 + (N - DF + .5)/(DF + .5)), based on Lucene
            .iMap { inNumDocs -> ln(1 + (numDocs - inNumDocs.toFloat() - 0.5f) / (inNumDocs.toFloat() + 0.5f)) }
    }

    /** Input is a count matrix */
    override fun transform(input: SimpleMatrix): SimpleMatrix {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }

        input.convertToSparse()
        val docLengths = input.sumRows()
        val avgDocLength = docLengths.elementSum() / input.numElements

        // TF: freq / (freq + k1 * (1 — b + b * dl / avgdl))
        return input.mapWithXY { row, col, d ->
            val tf = (d / (d + (k * (1 - b + (b * docLengths[row] / avgDocLength)))))
            tf * idf[col]
        }
    }
}