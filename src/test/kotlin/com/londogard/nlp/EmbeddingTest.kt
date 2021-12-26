package com.londogard.nlp

import com.github.benmanes.caffeine.cache.Caffeine
import com.londogard.nlp.embeddings.BpeEmbeddings
import com.londogard.nlp.embeddings.EmbeddingLoader
import com.londogard.nlp.embeddings.LightWordEmbeddings
import com.londogard.nlp.embeddings.WordEmbeddings
import com.londogard.nlp.utils.LanguageSupport
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import java.nio.file.Path
import kotlin.test.Test

class EmbeddingTest {
    @Test
    fun testBpeEmb() {
        val embeddings = EmbeddingLoader.fromLanguageOrNull<BpeEmbeddings>(LanguageSupport.sv)

        embeddings shouldNotBe null

        embeddings?.vector("hej") shouldNotBe null
        embeddings?.subwordVector("h") shouldNotBe null
    }

    @Test
    fun testLightWordEmbeddings() {
        val embeddings = LightWordEmbeddings(Path.of(javaClass.getResource("/sv_embeddings_cut.txt")!!.toURI()), maxWordCount = 1)

        embeddings.embeddings.size shouldBe 1
        embeddings.contains("hej") shouldBe true
        embeddings.addWords(setOf("då"))
        embeddings.cache.cleanUp()
        embeddings.embeddings.size shouldBe 1
        embeddings.contains("då") shouldBe true
        embeddings.contains("hej") shouldBe false
        embeddings.vector("då")?.shape?.get(0) shouldBe 3
    }

    @Test
    fun testWordEmbeddings() {
        val embeddings = WordEmbeddings(Path.of(javaClass.getResource("/sv_embeddings_cut.txt")!!.toURI()))
        embeddings.embeddings.size shouldBe 2
        embeddings.contains("hej") shouldBe true
        embeddings.contains("då") shouldBe true
        embeddings.contains("Då") shouldBe false
        embeddings.vector("då")?.shape?.get(0) shouldBe 3
    }
}