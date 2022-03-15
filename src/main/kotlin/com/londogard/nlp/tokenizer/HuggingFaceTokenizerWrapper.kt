package com.londogard.nlp.tokenizer

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer

class HuggingFaceTokenizerWrapper(val tokenizer: HuggingFaceTokenizer): Tokenizer {
    override fun split(text: String): List<String> {
        return tokenizer.encode(text).tokens.asList()
    }

    override fun batchSplit(texts: List<String>): List<List<String>> =
        tokenizer.batchEncode(texts).map { it.tokens.asList() }
}