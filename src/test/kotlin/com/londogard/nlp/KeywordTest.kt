package com.londogard.nlp
import com.londogard.nlp.keywords.CooccurrenceKeywords
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test
class KeywordTest {
    @Test
    fun testCooccurrenceKeywords() {
        val keywords = CooccurrenceKeywords.keywords("Londogard NLP toolkit is works on multiple languages.\nAn amazing piece of NLP tech.\nThis is how to fetch keywords! ")

        keywords shouldBeEqualTo listOf(listOf("nlp") to 2)
    }
}