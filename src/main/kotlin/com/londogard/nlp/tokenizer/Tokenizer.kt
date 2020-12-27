package com.londogard.nlp.tokenizer

interface Tokenizer {
    fun split(text: String): List<String>
}