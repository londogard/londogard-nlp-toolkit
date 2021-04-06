package com.londogard.nlp

import com.londogard.nlp.stopwords.Stopwords
import com.londogard.nlp.utils.LanguageSupport
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBe
import kotlin.test.Test

class StopwordTests {
    @Test
    fun testFullStopwords() {
        val stopwords = Stopwords.stopwordsOrNull(LanguageSupport.tr)

        stopwords shouldNotBe null
        stopwords!! shouldContain "acaba"
    }

    @Test
    fun testStopwords() {
        Stopwords.isStopword("acaba", LanguageSupport.tr) shouldBe true
        Stopwords.isStopword("d", LanguageSupport.tr) shouldBe false
    }
}