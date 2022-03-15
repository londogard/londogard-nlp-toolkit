package com.londogard.nlp

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
import com.londogard.nlp.tokenizer.*
import com.londogard.nlp.utils.LanguageSupport
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test

class TokenizerTests {
    @Test
    fun testCharTokenizer() {
        val tokenizer = CharTokenizer()

        tokenizer.split("abc") shouldBeEqualTo listOf("a", "b", "c")
        tokenizer.split("a bc") shouldBeEqualTo listOf("a", " ", "b", "c")
    }

    @Test
    fun testSimpleTokenizer() {
        val tokenizer = SimpleTokenizer()

        tokenizer.split("abc") shouldBeEqualTo listOf("abc")
        tokenizer.split("a bc") shouldBeEqualTo listOf("a", "bc")
        tokenizer.split("and, some") shouldBeEqualTo listOf("and", ",", "some")
    }

    @Test
    fun testSentencePieceTokenizer() {
        val tokenizer = SentencePieceTokenizer.fromLanguageSupportAndSizeOrNull(LanguageSupport.sv, VocabSize.v1000)

        tokenizer?.split("hej där borta?") shouldBeEqualTo listOf("▁h", "e", "j", "▁där", "▁b", "or", "ta", "?")
    }

    @Test
    fun testHuggingFaceTokenizer() {
        val tokenizer = HuggingFaceTokenizerWrapper(HuggingFaceTokenizer.newInstance("bert-base-uncased"))

        tokenizer.split("Hello over there!") shouldBeEqualTo listOf("[CLS]", "hello", "over", "there", "!", "[SEP]")
    }
}