package com.londogard.nlp

import com.londogard.nlp.embeddings.WordEmbeddings
import com.londogard.nlp.embeddings.sentence.AverageSentenceEmbeddings
import com.londogard.nlp.embeddings.sentence.USifSentenceEmbeddings
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.avgNorm
import com.londogard.nlp.utils.norm2
import com.londogard.nlp.wordfreq.WordFrequencies
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import java.nio.file.Path
import kotlin.test.Test

class SentenceEmbeddingTests {
    @Test
    fun testUsifEmbeddings() {
        val embeddings = WordEmbeddings(Path.of(javaClass.getResource("/sv_embeddings_cut.txt")!!.toURI()))
        val usif = USifSentenceEmbeddings(embeddings, WordFrequencies.getAllWordFrequenciesOrNull(LanguageSupport.sv) ?: emptyMap())
        val avgSentenceEmbedding = AverageSentenceEmbeddings(embeddings)
        val embedding = usif.getSentenceEmbedding(listOf("hej", "där", "borta"))
        val rawData = embedding.data

        rawData[0] shouldNotBeEqualTo rawData[1]
        rawData.size shouldBe 3

        usif.getSentenceEmbedding(listOf("hej", "då")).toString() shouldNotBeEqualTo avgSentenceEmbedding.getSentenceEmbedding(listOf("hej", "då")).toString()
    }

    @Test
    fun testAvgSentenceEmbeddings() {
        val embeddings = WordEmbeddings(Path.of(javaClass.getResource("/sv_embeddings_cut.txt")!!.toURI()))
        val avgSentenceEmbeddings = AverageSentenceEmbeddings(embeddings)

        embeddings.traverseVectors(listOf("hej", "då")).avgNorm().toString() shouldBeEqualTo avgSentenceEmbeddings.getSentenceEmbedding(listOf("hej", "då")).toString()
        val simpleVector = embeddings.vector("hej")!!
        (simpleVector / simpleVector.norm2()).toString() shouldBeEqualTo avgSentenceEmbeddings.getSentenceEmbedding(listOf("hej")).toString()
    }
}