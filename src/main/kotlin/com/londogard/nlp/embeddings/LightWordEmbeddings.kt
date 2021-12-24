package com.londogard.nlp.embeddings

import com.londogard.nlp.embeddings.EmbeddingLoader.FastTextDefaultEmbeddingDimension
import com.londogard.nlp.utils.caches.LRUCache
import com.londogard.nlp.utils.caches.PerpetualCache
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import java.nio.file.Path

class LightWordEmbeddings(
    override val filePath: Path,
    override val dimensions: Int = FastTextDefaultEmbeddingDimension,
    override val delimiter: Char = ' ',
    private val maxWordCount: Int = 1000
) : Embeddings {
    override val embeddings: LRUCache<String, D1Array<Float>> = LRUCache(PerpetualCache(HashMap(maxWordCount, 0.75f)), maxWordCount)
    override val vocabulary: Set<String> by lazy { embeddings.keys }

    init { loadEmbeddings(inFilter = emptySet()) }

    fun addWords(words: Set<String>) {
        val embeddingKeys = embeddings.keys
        val leftToAdd = words - embeddingKeys

        if (leftToAdd.isNotEmpty() && leftToAdd.size + embeddings.size > maxWordCount) {
            val toRemove = (embeddingKeys - words).take(leftToAdd.size + embeddings.size - maxWordCount)

            embeddings -= toRemove.toSet()
        }

        if (leftToAdd.isNotEmpty()) {
            loadEmbeddings(leftToAdd)
        }
    }

    private fun loadEmbeddings(inFilter: Set<String>) {
        embeddings.putAll(EmbeddingLoader.fromFile(filePath, delimiter, inFilter, maxWordCount))
    }
}