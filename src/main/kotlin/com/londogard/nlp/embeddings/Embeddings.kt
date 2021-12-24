package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.caches.Cache
import com.londogard.nlp.utils.cosineDistance
import com.londogard.nlp.utils.euclideanDistance
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import java.nio.file.Path

interface Embeddings {
    val dimensions: Int
    val delimiter: Char
    val filePath: Path

    val embeddings: Cache<String, D1Array<Float>>
    val vocabulary: Set<String>

    /** Check if the word has a embedding. */
    fun contains(word: String): Boolean = embeddings.contains(word)

    fun vector(word: String): D1Array<Float>? = embeddings[word]

    // OBS: Will return all possible vectors, not necessarily ALL
    fun traverseVectors(words: List<String>): List<D1Array<Float>> = words.mapNotNull(::vector)

    fun traverseVectorsOrNull(words: List<String>): List<D1Array<Float>>? =
        words.fold(emptyList<D1Array<Float>>() as List<D1Array<Float>>?) {
            acc, word -> if (acc == null) acc else vector(word)?.let { vec -> acc + vec }
        }

    fun euclideanDistance(w1: String, w2: String): Float? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().euclideanDistance(vectors.last()) }

    fun cosineDistance(w1: String, w2: String): Float? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().cosineDistance(vectors.last()) }
}