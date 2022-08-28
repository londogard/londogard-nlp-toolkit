package com.londogard.nlp.meachinelearning.predictors.transformers.translators

import ai.djl.ndarray.NDList
import ai.djl.translate.Translator
import ai.djl.translate.TranslatorContext
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper

class TokenClassificationTranslator(val tokenizer: HuggingFaceTokenizerWrapper, val config: Map<Int, String>) :
    Translator<String, List<String>> {
    override fun processInput(ctx: TranslatorContext, input: String): NDList {
        val inputEncoding = tokenizer.encode(input)
        val ids = ctx.ndManager.create(inputEncoding.ids)
        val attention = ctx.ndManager.create(inputEncoding.attentionMask)
        val tokenTypeIds = ctx.ndManager.create(inputEncoding.typeIds)

        return NDList(ids, attention, tokenTypeIds)
    }

    override fun processOutput(ctx: TranslatorContext, list: NDList): List<String> {
        return list.map { tokenClass ->
            val tokenCategory = tokenClass.softmax(1).argMax(1).toLongArray()
            val nonClsSep = 1 until tokenCategory.size - 1

            return tokenCategory.slice(nonClsSep).map { cat -> config.getOrElse(cat.toInt()) { cat.toString() } }
        }
    }
}