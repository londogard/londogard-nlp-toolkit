package com.londogard.nlp.embeddings

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.londogard.nlp.embeddings.EmbeddingLoader.FastTextDefaultEmbeddingDimension
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import java.nio.file.Path

class LightWordEmbeddings(
    override val filePath: Path,
    override val dimensions: Int = FastTextDefaultEmbeddingDimension,
    override val delimiter: Char = ' ',
    private val maxWordCount: Int = 1000
) : Embeddings {
    internal val cache: Cache<String, D1Array<Float>> = Caffeine.newBuilder()
        .maximumSize(maxWordCount.toLong())
        .build()
    override val embeddings: MutableMap<String, D1Array<Float>> = cache.asMap()
    override val vocabulary: Set<String> by lazy { embeddings.keys }

    init { loadEmbeddings(inFilter = emptySet()) }

    fun addWords(words: Set<String>) {
        val embeddingKeys = embeddings.keys
        val leftToAdd = words - embeddingKeys

        if (leftToAdd.isNotEmpty())
            loadEmbeddings(leftToAdd)
    }

    private fun loadEmbeddings(inFilter: Set<String>) {
        embeddings.putAll(EmbeddingLoader.fromFile(filePath, delimiter, inFilter, maxWordCount))
    }
}