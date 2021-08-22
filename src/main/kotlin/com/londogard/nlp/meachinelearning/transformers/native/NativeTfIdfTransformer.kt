package com.londogard.nlp.meachinelearning.transformers.native

import ai.djl.ndarray.NDArray
import ai.djl.ndarray.types.DataType
import ai.djl.ndarray.types.Shape
import ai.djl.ndarray.types.SparseFormat
import com.londogard.nlp.meachinelearning.NotFitException

// Based on SkLearns TfIdf variant. Missing norm option
class NativeTfIdfTransformer : NativeTransformer {
    lateinit var idf: NDArray

    override fun fit(input: NDArray) {
        val sparseInput = if (input.isSparse) input else input.toSparse(SparseFormat.CSR)
        val numDocs = input.shape[0]

        val df = sparseInput
            .toType(DataType.BOOLEAN, false)
        TODO("")


        val idfP = df
            .addi(1).divi(1 / numDocs).addi(1)
            .log()

        val indices = LongArray(sparseInput.shape[1].toInt()) { it.toLong() }
        idf = idfP.manager.createCoo(idfP.toByteBuffer(), arrayOf(indices, indices), Shape(input.shape[1], input.shape[1]))
    }

    /** Input is a count matrix */
    override fun transform(input: NDArray): NDArray {
        if (!::idf.isInitialized) {
            throw NotFitException("TfIdfVectorizer must be 'fit' before calling 'transform'!")
        }

        val sparseInput = if (input.isSparse) input else input.toSparse(SparseFormat.COO)

        //val colIndices = output.colIndices
//
        //for (col in 0 until output.colIndices.size - 1) {
        //    val colIdf = idf[col]
        //    for (i in colIndices[col] until colIndices[col + 1])
        //        output.data[i] *= colIdf
        //}

        return sparseInput.mul(idf)
    }
}