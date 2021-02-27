package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.*
import org.ejml.data.FMatrixRMaj
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asSequence

abstract class Embeddings {
    abstract val dimensions: Int
    abstract val delimiter: Char
    abstract val normalized: Boolean
    abstract val filename: String

    internal abstract val embeddings: Map<String, Int>
    internal abstract val ndArray: FMatrixRMaj

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
    fun vector(word: String): FMatrixRMaj? = embeddings[word]?.let(ndArray::getRow)

    fun vectors(words: List<String>) : FMatrixRMaj = ndArray.getRows(words.mapNotNull(embeddings::get).toIntArray())

    fun traverseVectors(words: List<String>): FMatrixRMaj = vectors(words)

    /** Compute the Euclidean distance between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The Euclidean distance between the vector representations of the words.
     */
    fun euclidean(w1: String, w2: String): Float? = traverseVectors(listOf(w1, w2))
        .takeIf { it.numRows == 2 }
        ?.let { vectors -> vectors.getRow(0).euclideanDistance(vectors.getRow(1)) }

    /** Compute the cosine similarity score between the vector representations of the words.
     * @param w1 The first word.
     * @param w2 The other word.
     * @return The cosine similarity score between the vector representations of the words.
     */
    fun cosineDistance(w1: String, w2: String): Float? = traverseVectors(listOf(w1, w2))
        .takeIf { it.numRows == 2 }
        ?.let { vectors -> vectors.getRow(0).cosineDistance(vectors.getRow(1)) }

    internal fun loadEmbeddingsFromFile(inFilter: Set<String> = emptySet(),
                                        maxWordCount: Int = Int.MAX_VALUE): Pair<Map<String, Int>, FMatrixRMaj> =
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
                .unzip()
                .let { (words, data) -> words.mapIndexed { i, word -> word to i }.toMap() to FMatrixRMaj(data.toTypedArray()) }
        }
}