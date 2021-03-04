package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.getRows
import org.ejml.data.MatrixType
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

abstract class LightWordEmbeddings(
    override val dimensions: Int = DefaultEmbeddingDimension,
    //override val filename: String = DownloadHelper.embeddingPath,
    override val delimiter: Char = ' ',
    override val normalized: Boolean = true,
    private val maxWordCount: Int = 1000
) : Embeddings() {
    // TODO fix ugly hack!
    override val embeddings: MutableMap<String, SimpleMatrix> = mutableMapOf()

    init {
    //    if (filename == DownloadHelper.embeddingPath && !DownloadHelper.embeddingsExist())
    //        DownloadHelper.downloadGloveEmbeddings()
        loadEmbeddings(inFilter = emptySet())
    }

    fun addWords(words: Set<String>) {
        val embeddingKeys = embeddings.keys
        val leftToAdd = words - embeddingKeys

        // TODO can be optimized by doing in-place updates to matrix
        if (leftToAdd.isNotEmpty() && leftToAdd.size + embeddings.size > maxWordCount) {
            val toRemove = (embeddings.keys - words).take(leftToAdd.size + embeddings.size - maxWordCount)

            embeddings -= toRemove
        }

        if (leftToAdd.isNotEmpty())
            loadEmbeddings(leftToAdd)
    }

    private fun loadEmbeddings(inFilter: Set<String>) {
        embeddings.putAll(loadEmbeddingsFromFile(inFilter, maxWordCount))
    }

    companion object {
        fun fromFilePath(path: Path): WordEmbeddings {
            TODO("")
        }
    }
}