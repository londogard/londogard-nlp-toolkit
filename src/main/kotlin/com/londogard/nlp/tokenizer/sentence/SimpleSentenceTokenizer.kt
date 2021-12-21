package com.londogard.nlp.tokenizer.sentence

import com.londogard.nlp.tokenizer.Tokenizer

class SimpleSentenceTokenizer: Tokenizer {
    override fun split(text: String): List<String> {
        return text
            .split(sentence)
            .map { it.replace(multiSpaces, " ") }
    }

    companion object {
        val multiSpaces: Regex = Regex("\\s+")
        val sentence: Regex = Regex("([.!?]\\s|[\n\r]+)")
    }
}