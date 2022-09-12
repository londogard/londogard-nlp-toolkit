package com.londogard.nlp.tokenizer.sentence

import com.londogard.nlp.preprocessing.Preprocessor.removeExtraSpaces
import com.londogard.nlp.tokenizer.Tokenizer

/** A sentence tokenizer which returns each sentence as a token using simple heuristics. */
class SimpleSentenceTokenizer: Tokenizer {
    override fun split(text: String): List<String> {
        return text
            .split(sentenceRegex)
            .map { sentence -> sentence.removeExtraSpaces() }
    }

    companion object {
        val sentenceRegex: Regex = Regex("([.!?]\\s|[\n\r]+)")
    }
}