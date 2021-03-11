package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.cosineDistance
import com.londogard.nlp.utils.euclideanDistance
import com.londogard.nlp.utils.useLines
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

abstract class Embeddings {
    abstract val dimensions: Int
    abstract val delimiter: Char
    abstract val filePath: Path

    internal abstract val embeddings: Map<String, SimpleMatrix>

    val vocabulary by lazy { embeddings.keys }

    /** Check if the word is present in the vocab map.
     * @param word Word to be checked.
     * @return True if the word is in the vocab map.
     */
    fun contains(word: String): Boolean = embeddings.contains(word)

    fun vector(word: String): SimpleMatrix? = embeddings[word]

    // OBS: Will return all possible vectors, not necessarily ALL
    fun traverseVectors(words: List<String>): List<SimpleMatrix> = words.mapNotNull(embeddings::get)

    fun traverseVectorsOrNull(words: List<String>): List<SimpleMatrix>? =
        words.mapNotNull(embeddings::get).takeIf { vectors -> vectors.size == words.size }

    /** Compute the Euclidean distance between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The Euclidean distance between the vector representations of the words.
     */
    fun euclidean(w1: String, w2: String): Double? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().euclideanDistance(vectors.last()) }

    /** Compute the cosine similarity score between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The cosine similarity score between the vector representations of the words.
     */
    fun cosineDistance(w1: String, w2: String): Double? = traverseVectorsOrNull(listOf(w1, w2))
        ?.let { vectors -> vectors.first().cosineDistance(vectors.last()) }
}