package com.londogard.nlp.embeddings

import com.londogard.nlp.tokenizer.SentencePieceTokenizer
import com.londogard.nlp.tokenizer.Tokenizer
import com.londogard.nlp.tokenizer.toVocabSize
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.avgNorm
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

class BpeEmbeddings(
    override val filePath: Path,
    override val dimensions: Int = EmbeddingLoader.BpeDefaultEmbeddingDimension,
    override val delimiter: Char = ' ',
    private val tokenizer: Tokenizer = toTokenizer(filePath)
) : Embeddings {
    override val embeddings: Map<String, SimpleMatrix> by lazy { EmbeddingLoader.fromFile(filePath, delimiter) }
    override val vocabulary: Set<String> by lazy { embeddings.keys }

    override fun vector(word: String): SimpleMatrix? {
        return tokenizer.split(word).mapNotNull { subword -> super.vector(subword) }
            .takeIf(List<SimpleMatrix>::isNotEmpty)
            ?.avgNorm()
    }

    override fun contains(word: String): Boolean {
        return tokenizer.split(word).all(embeddings::contains)
    }

    fun subwordVector(subword: String): SimpleMatrix? = embeddings[subword]

    companion object {
        @JvmStatic fun toTokenizer(filePath: Path): Tokenizer {
            val rawNameTokens = filePath.fileName.toString().split('.')

            val languageSupport = LanguageSupport.valueOf(rawNameTokens.first())
            val vocabSize = rawNameTokens.find { token -> token.startsWith("vs") }?.removePrefix("vs")?.toInt()?.toVocabSize()
                ?: throw IllegalArgumentException("BpeEmbeddings must use standard vocab-size or supply tokenizer argument manually!")

            return SentencePieceTokenizer.fromLanguageSupportAndSizeOrNull(languageSupport, vocabSize)
                ?: throw IllegalArgumentException("BpeEmbeddings must have standard SentencePieceTokenizer or supply tokenizer argument manually!")
        }
    }
}