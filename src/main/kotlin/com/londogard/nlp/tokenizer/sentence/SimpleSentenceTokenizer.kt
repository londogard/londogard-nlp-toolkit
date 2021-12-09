package com.londogard.nlp.tokenizer.sentence

import com.londogard.nlp.tokenizer.Tokenizer

class SimpleSentenceTokenizer: Tokenizer {
    override fun split(text: String): List<String> {
        return (text
            .replace(forgottenSpaces, "$1$2 $3") + "\n")
            .replace(multiSpaces, " ")
            .split(sentence)
    }

    companion object {
        val newLines: Regex = Regex("[\\n\\r]+")
        val multiSpaces: Regex = Regex("\\s+")
        val forgottenSpaces: Regex = Regex("(.)([\\.!?])([\\D&&\\S&&[^\\.\"'`\\)\\}\\]]])")
        val sentence: Regex = Regex("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s")
    }
}