package com.londogard.nlp.embeddings

import com.londogard.nlp.tokenizer.SentencePieceTokenizer
import com.londogard.nlp.tokenizer.Tokenizer
import com.londogard.nlp.tokenizer.toVocabSize
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.avgNorm
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import java.nio.file.Path

/**
 * BPEEmbeddings are subword embeddings that embeds [SentencePieceTokenizer] tokenized data.
 * Studies show that performance are on par with GloVe (+-5%) while only using few MB's of data rather than GB's.
 * Supports 275 languages through [bpemb](https://bpemb.h-its.org/).
 */
class BpeEmbeddings(
    override val filePath: Path,
    override val dimensions: Int = EmbeddingLoader.BpeDefaultEmbeddingDimension,
    override val delimiter: Char = ' ',
    private val tokenizer: Tokenizer = toTokenizer(filePath)
) : Embeddings {
    override val embeddings: Map<String, D1Array<Float>> by lazy { EmbeddingLoader.fromFile(filePath, delimiter) }
    override val vocabulary: Set<String> by lazy { embeddings.keys }

    override fun vector(word: String): D1Array<Float>? {
        return tokenizer.split(word).mapNotNull { subword -> super.vector(subword) }
            .takeIf(List<*>::isNotEmpty)
            ?.avgNorm()
    }

    override fun contains(word: String): Boolean {
        return tokenizer.split(word).all(embeddings::contains)
    }

    fun subwordVector(subword: String): D1Array<Float>? = embeddings[subword]

    companion object {
        @JvmStatic
        fun toTokenizer(filePath: Path): Tokenizer {
            val rawNameTokens = filePath.fileName.toString().split('.')

            val languageSupport = LanguageSupport.valueOf(rawNameTokens.first())
            val vocabSize =
                rawNameTokens.find { token -> token.startsWith("vs") }?.removePrefix("vs")?.toInt()?.toVocabSize()
                    ?: throw IllegalArgumentException("BpeEmbeddings must use standard vocab-size or supply tokenizer argument manually!")

            return SentencePieceTokenizer.fromLanguageSupportAndSizeOrNull(languageSupport, vocabSize)
                ?: throw IllegalArgumentException("BpeEmbeddings must have standard SentencePieceTokenizer or supply tokenizer argument manually!")
        }
    }
}