package com.londogard.nlp.tokenizer

class CharTokenizer : Tokenizer {
    override fun split(text: String): List<String> = text.map(Char::toString)
}