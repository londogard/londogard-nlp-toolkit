package com.londogard.nlp.tokenizer

import ai.djl.sentencepiece.SpTokenizer
import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import java.nio.file.Path
import kotlin.io.path.readLines

/**
 * A [SentencePiece Tokenizer](https://github.com/google/sentencepiece).  
 * This is a subword-tokenizer meaning that it return subword-tokens, e.g. "hey" might end up "h", "ey". 
*/
class SentencePieceTokenizer(modelPath: Path, vocabPath: Path? = null): Tokenizer {
    private val sentencePieceTokenizer = SpTokenizer(modelPath)
    val vocab: Set<String>? = vocabPath
        ?.readLines()
        ?.filter(String::isNotBlank)
        ?.map { line -> line.split('\t', limit = 2)[0] }
        ?.toSet()

    override fun split(text: String): List<String> = sentencePieceTokenizer.tokenize(text)

    companion object {
        const val beginningOfWord: Char = '▁'
        @JvmStatic fun fromLanguageSupportOrNull(languageSupport: LanguageSupport): SentencePieceTokenizer? =
            fromLanguageSupportAndSizeOrNull(languageSupport, VocabSize.v10_000)
        @JvmStatic fun fromLanguageSupportAndSizeOrNull(languageSupport: LanguageSupport, vocabSize: VocabSize) =
            if (languageSupport.hasSentencePiece()) {
                val (vocab, model) = DownloadHelper.getSentencePieceVocabModel(languageSupport, vocabSize.size)
                SentencePieceTokenizer(model, vocab)
            } else null
    }
}

fun Int.toVocabSize(): VocabSize? =
    VocabSize.values().find { size -> size.size == this }

enum class VocabSize(val size: Int) {
    v1000(1000),
    v3000(3000),
    v5000(5000),
    v10_000(10_000),
    v25_000(25_000),
    v50_000(50_000),
    v100_000(100_000),
    v200_000(200_000)
}