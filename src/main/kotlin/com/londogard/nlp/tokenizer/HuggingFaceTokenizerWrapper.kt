package com.londogard.nlp.tokenizer

import ai.djl.huggingface.tokenizers.Encoding
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer

class HuggingFaceTokenizerWrapper(val tokenizer: HuggingFaceTokenizer): Tokenizer {
    constructor(modelName: String): this(HuggingFaceTokenizer.newInstance(modelName))

    override fun split(text: String): List<String> {
        return tokenizer.encode(text).tokens.asList()
    }

    fun encode(text: String): Encoding = tokenizer.encode(text)

    fun batchEncode(texts: List<String>): Array<Encoding> = tokenizer.batchEncode(texts)

    override fun batchSplit(texts: List<String>): List<List<String>> =
        tokenizer.batchEncode(texts).map { it.tokens.asList() }
}