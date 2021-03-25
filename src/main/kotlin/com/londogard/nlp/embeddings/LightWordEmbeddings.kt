package com.londogard.nlp.embeddings

import com.londogard.nlp.embeddings.EmbeddingLoader.FastTextDefaultEmbeddingDimension
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

class LightWordEmbeddings(
    override val filePath: Path,
    override val dimensions: Int = FastTextDefaultEmbeddingDimension,
    override val delimiter: Char = ' ',
    private val maxWordCount: Int = 1000
) : Embeddings {
    override val embeddings: MutableMap<String, SimpleMatrix> = mutableMapOf()
    override val vocabulary: Set<String> by lazy { embeddings.keys }

    init { loadEmbeddings(inFilter = emptySet()) }

    fun addWords(words: Set<String>) {
        val embeddingKeys = embeddings.keys
        val leftToAdd = words - embeddingKeys

        if (leftToAdd.isNotEmpty() && leftToAdd.size + embeddings.size > maxWordCount) {
            val toRemove = (embeddings.keys - words).take(leftToAdd.size + embeddings.size - maxWordCount)

            embeddings -= toRemove
        }

        if (leftToAdd.isNotEmpty())
            loadEmbeddings(leftToAdd)
    }

    private fun loadEmbeddings(inFilter: Set<String>) {
        embeddings.putAll(EmbeddingLoader.fromFile(filePath, delimiter, inFilter, maxWordCount))
    }
}