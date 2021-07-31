package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.vectorizer.CountVectorizer
import com.londogard.nlp.utils.numElems
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test


class CountVectorizerTest {
    @Test
    fun testSimpleVec() {
        val mat = listOf(arrayOf("1", "2", "3"), arrayOf("1", "2", "4"), arrayOf("5"))
        val countMatrix = CountVectorizer<Float>().fitTransform(mat)
        countMatrix[0,0] shouldBeEqualTo 1.0
        countMatrix[0,1] shouldBeEqualTo 1.0
        countMatrix[2,0] shouldBeEqualTo 0.0          // validate that sparse matrix still return 0:s

        countMatrix.numElems() shouldBeEqualTo 7 // validate it's sparse
        countMatrix.numCols() * countMatrix.numRows() shouldBeEqualTo 15 // validate that shape is correct
    }
}