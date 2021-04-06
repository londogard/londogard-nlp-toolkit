package com.londogard.nlp

import com.londogard.nlp.embeddings.EmbeddingLoader
import com.londogard.nlp.tokenizer.VocabSize
import com.londogard.nlp.utils.FileInfo
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.UrlProvider
import com.londogard.nlp.wordfreq.WordFrequencySize
import org.amshove.kluent.shouldBe
import java.net.HttpURLConnection
import kotlin.test.Test

class UrlProviderTest {
    @Test
    fun testFastText() {
        val fileInfo = UrlProvider.fastText(LanguageSupport.sv)
        checkUrlWorks(fileInfo) shouldBe true
    }

    @Test
    fun testSentencePiece() {
        val (vocab, model) = UrlProvider.sentencePiece(LanguageSupport.sv, VocabSize.v1000.size)

        checkUrlWorks(vocab) shouldBe true
        checkUrlWorks(model) shouldBe true
    }

    @Test
    fun testBpeEmbedding() {
        val fileInfo = UrlProvider.bpeEmbedding(LanguageSupport.sv, VocabSize.v1000.size, EmbeddingLoader.BpeDefaultEmbeddingDimension)

        checkUrlWorks(fileInfo) shouldBe true
    }

    @Test
    fun testStopwords() {
        val fileInfo = UrlProvider.stopwords(LanguageSupport.sv)

        checkUrlWorks(fileInfo) shouldBe true
    }

    @Test
    fun testWordFreq() {
        val fileInfo = UrlProvider.wordfreq(LanguageSupport.sv, WordFrequencySize.Smallest)

        checkUrlWorks(fileInfo) shouldBe true
    }

    private fun checkUrlWorks(fileInfo: FileInfo): Boolean {
        val url = fileInfo.toUrl()
        println(fileInfo.url)
        val connection: HttpURLConnection = (url.openConnection() as HttpURLConnection).apply { requestMethod = "HEAD" }

        return connection.responseCode == 200
    }
}