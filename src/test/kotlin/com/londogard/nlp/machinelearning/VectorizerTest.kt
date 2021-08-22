package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.vectorizer.Bm25Vectorizer
import com.londogard.nlp.meachinelearning.vectorizer.TfIdfVectorizer
import com.londogard.nlp.meachinelearning.vectorizer.count.CountVectorizer
import com.londogard.nlp.tokenizer.SimpleTokenizer
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.ndarray.operations.asSequence
import org.junit.Test
import kotlin.test.assertEquals

class VectorizerTest {
    val simpleTok = SimpleTokenizer()
    val simpleTexts = listOf("hejsan jag älskar sverige", "hej vad bra det är i sverige", "jag älskar sverige", "jag hatar norge", "norge hatar", "norge hatar", "norge hatar")
        .map(simpleTok::split)

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

    @Test
    fun testCount() {
        val countVect = CountVectorizer<Float>()
        val lhs = countVect.fitTransform(simpleTexts).asSequence().toList()

        lhs shouldBeEqualTo countVect.transform(simpleTexts).asSequence().toList()
        lhs.slice(0 until countVect.vectorization.size) shouldBeEqualTo listOf(1f,1f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f)
    }

    @Test
    fun testTfIdf() {
        val tfidf = TfIdfVectorizer<Float>()
        val lhs = tfidf.fitTransform(simpleTexts).asSequence().toList()

        lhs shouldBeEqualTo tfidf.transform(simpleTexts).asSequence().toList()
        assertEquals(lhs.first(), 2.25276f, 1e-4f)
    }

    @Test
    fun testBm25() {
        val bm25 = Bm25Vectorizer<Float>(k = 3, b = 0.1f)
        val lhs = bm25.fitTransform(simpleTexts).asSequence().toList()

        lhs shouldBeEqualTo bm25.transform(simpleTexts).asSequence().toList()
        assertEquals(lhs.first(), 0.5115f, 1e-4f)
    }
}