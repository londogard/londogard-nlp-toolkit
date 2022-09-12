package com.londogard.nlp.tokenizer

/** A Character Tokenizer which returns a token for each character in the string. */
class CharTokenizer : Tokenizer {
    override fun split(text: String): List<String> = text.map(Char::toString)
}