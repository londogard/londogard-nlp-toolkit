package com.londogard.nlp.meachinelearning.predictors.transformers.translators

import ai.djl.ndarray.NDList
import ai.djl.translate.Translator
import ai.djl.translate.TranslatorContext
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper

class ClassifierTranslator(private val tokenizer: HuggingFaceTokenizerWrapper, private val config: Map<Int, String>) :
    Translator<String, String> {
    override fun processInput(ctx: TranslatorContext, input: String): NDList {
        val inputEncoding = tokenizer.encode(input)
        val ids = ctx.ndManager.create(inputEncoding.ids)
        val attention = ctx.ndManager.create(inputEncoding.attentionMask)

        return NDList(ids, attention)
    }

    override fun processOutput(ctx: TranslatorContext, list: NDList): String {
        val data = list[0].softmax(0)
        val category = data.argMax().toLongArray()[0].toInt()

        return config.getOrDefault(category, category.toString())
    }
}