package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.vectorizer.count.CountVectorizer
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test

class CountVectorizerTest {
    @Test
    fun testSimpleVec() {
        val mat = listOf(listOf("1", "2", "3"), listOf("1", "2", "4"), listOf("5"))
        val countMatrix = CountVectorizer<Float>().fitTransform(mat)
        countMatrix[0,0] shouldBeEqualTo 1f
        countMatrix[0,1] shouldBeEqualTo 1f

        countMatrix[2,0] shouldBeEqualTo 0f          // validate that sparse matrix still return 0:s

        countMatrix.data.size shouldBeEqualTo 7 // validate it's sparse
        countMatrix.size shouldBeEqualTo 15 // validate that shape is correct
    }
}