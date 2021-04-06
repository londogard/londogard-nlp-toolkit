package com.londogard.nlp

import com.londogard.nlp.stemmer.Stemmer
import com.londogard.nlp.utils.LanguageSupport
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test

class StemmerTests {
    @Test
    fun testStemmer() {
        val stemmer = Stemmer(LanguageSupport.sv)

        stemmer.stem("hej") shouldBeEqualTo "hej"
        stemmer.stem("katten") shouldBeEqualTo "katt"
    }

    @Test
    fun testStemmerObject() {
        Stemmer.stem("hej", LanguageSupport.sv) shouldBeEqualTo "hej"
        Stemmer.stem("katten", LanguageSupport.sv) shouldBeEqualTo "katt"
    }
}