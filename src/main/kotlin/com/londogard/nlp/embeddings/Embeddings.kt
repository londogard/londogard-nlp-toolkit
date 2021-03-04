package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.cosineDistance
import com.londogard.nlp.utils.euclideanDistance
import org.ejml.simple.SimpleMatrix
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asSequence

abstract class Embeddings {
    abstract val dimensions: Int
    abstract val delimiter: Char
    abstract val normalized: Boolean
    abstract val filename: String

    internal abstract val embeddings: Map<String, SimpleMatrix>

    /** Vocabulary of the embeddings */
    val vocabulary by lazy { embeddings.keys }

    /** Check if the word is present in the vocab map.
     * @param word Word to be checked.
     * @return True if the word is in the vocab map.
     */
    fun contains(word: String): Boolean = embeddings.contains(word)

    /** Get the vector representation for the word.
     * @param word Word to retrieve vector for.
     * @return The vector representation of the word.
     */
    fun vector(word: String): SimpleMatrix? = embeddings[word]

    fun traverseVectors(words: List<String>): List<SimpleMatrix> = words.mapNotNull(embeddings::get)

    /** Compute the Euclidean distance between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The Euclidean distance between the vector representations of the words.
     */
    fun euclidean(w1: String, w2: String): Double? = traverseVectors(listOf(w1, w2))
        .takeIf { lines -> lines.size == 2 }
        ?.let { vectors -> vectors.first().euclideanDistance(vectors.last()) }

    /** Compute the cosine similarity score between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The cosine similarity score between the vector representations of the words.
     */
    fun cosineDistance(w1: String, w2: String): Double? = traverseVectors(listOf(w1, w2))
        .takeIf { lines -> lines.size == 2 }
        ?.let { vectors -> vectors.first().cosineDistance(vectors.last()) }

    internal fun loadEmbeddingsFromFile(inFilter: Set<String> = emptySet(),
                                        maxWordCount: Int = Int.MAX_VALUE): Map<String, SimpleMatrix> =
        Files
        .newBufferedReader(Paths.get(filename))
        .use { reader ->
            reader
                .lines()
                .filter { line -> inFilter.isEmpty() || inFilter.contains(line.takeWhile { it != delimiter }) }
                .asSequence()
                .mapNotNull { line ->
                    line
                        .split(delimiter)
                        .filterNot { it.isEmpty() || it.isBlank() }
                        .takeIf { it.size > dimensions }
                        ?.let { elems ->
                            val key = elems.first()
                            val value = FloatArray(dimensions) { i -> elems[i + 1].toFloat() }
                            key to value
                        }
                }
                .take(maxWordCount)
                .map { (key, value) -> key to SimpleMatrix(1, dimensions, true, value) }
                .toMap()
        }
}