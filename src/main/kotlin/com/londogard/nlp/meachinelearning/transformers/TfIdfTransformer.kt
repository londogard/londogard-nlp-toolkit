package com.londogard.nlp.meachinelearning.transformers

import com.londogard.nlp.meachinelearning.KmathUtils.toSparse
import com.londogard.nlp.meachinelearning.KmathUtils.efficientSparseBuildMatrix
import com.londogard.nlp.meachinelearning.KmathUtils.numElements
import com.londogard.nlp.meachinelearning.NotFitException
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.asIterable
import kotlin.math.ln

class TfIdfTransformer() : BaseTransformer<Float, Float> {
    lateinit var idf: Point<Float>

    /** Input is a count matrix */
    override fun transform(input: Matrix<Float>): Matrix<Float> {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }
        val inputSparse = input.toSparse()

        return efficientSparseBuildMatrix(input.rowNum, input.colNum, inputSparse.numElements()) {
                i, j -> inputSparse[i,j] * idf[j]
        }
    }

    override fun fit(input: Matrix<Float>) {
        val inputSparse = input.toSparse()
        val numDocs = input.rowNum

        idf = inputSparse
            .columns
            .map { col -> col.asIterable().count { it > 0 } }
            .map { inNumDocs -> ln(numDocs.toFloat() / (inNumDocs + 1)) + 1 }
            .toFloatArray().asBuffer()
    }
}