package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.cosineDistance
import com.londogard.nlp.utils.euclideanDistance
import com.londogard.nlp.utils.useLines
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

interface Embeddings {
    val dimensions: Int
    val delimiter: Char
    val filePath: Path

    val embeddings: Map<String, SimpleMatrix>
    val vocabulary: Set<String>

    /** Check if the word has a embedding. */
    fun contains(word: String): Boolean = embeddings.contains(word)

    fun vector(word: String): SimpleMatrix? = embeddings[word]

    // OBS: Will return all possible vectors, not necessarily ALL
    fun traverseVectors(words: List<String>): List<SimpleMatrix> = words.mapNotNull(::vector)

    fun traverseVectorsOrNull(words: List<String>): List<SimpleMatrix>? =
        words.fold(emptyList<SimpleMatrix>() as List<SimpleMatrix>?) {
            acc, word -> if (acc == null) acc else vector(word)?.let { vec -> acc + vec }
        }

    fun euclideanDistance(w1: String, w2: String): Double? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().euclideanDistance(vectors.last()) }

    fun cosineDistance(w1: String, w2: String): Double? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().cosineDistance(vectors.last()) }
}