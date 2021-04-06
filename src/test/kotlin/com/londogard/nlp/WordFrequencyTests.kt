package com.londogard.nlp

import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.wordfreq.WordFrequencies
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBe
import kotlin.test.Test

class WordFrequencyTests {
    @Test fun testFullWordFreq() {
        val wordFreqNb = WordFrequencies.getAllWordFrequenciesOrNull(LanguageSupport.nb)

        wordFreqNb shouldNotBe null
        wordFreqNb?.containsKey("er") shouldBe true
    }

    @Test fun testSingleWordFreq() {
        WordFrequencies.wordFrequencyOrNull("er", LanguageSupport.nb) shouldNotBe null
    }

    @Test fun testSingleZipf() {
        WordFrequencies.zipfFrequencyOrNull("er", LanguageSupport.nb) shouldNotBe null
    }
}