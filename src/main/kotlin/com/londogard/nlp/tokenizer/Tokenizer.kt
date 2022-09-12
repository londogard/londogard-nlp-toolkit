package com.londogard.nlp.tokenizer

/** Tokenize a string into multiple tokens */
interface Tokenizer {
    fun split(text: String): List<String>

    /** A more efficient approach for native tokenizers, i.e. HuggingFaceTokenizer */
    fun batchSplit(texts: List<String>): List<List<String>> = texts.map(this::split)
}