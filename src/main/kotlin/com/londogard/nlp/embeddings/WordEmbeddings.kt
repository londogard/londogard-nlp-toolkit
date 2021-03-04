package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.*
import org.ejml.kotlin.*
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

// TODO fix languageSupport etc
abstract class WordEmbeddings(
    override val dimensions: Int = DefaultEmbeddingDimension,
    //override val filename: String = DownloadHelper.embeddingPath,
    override val delimiter: Char = ' ',
    override val normalized: Boolean = true
) : Embeddings() {
    /** Vocabulary, word to embedded space */
    override val embeddings: Map<String, SimpleMatrix> by lazy { loadEmbeddingsFromFile() }

    init {
//        if (filename == DownloadHelper.embeddingPath && !DownloadHelper.embeddingsExist())
//            DownloadHelper.downloadGloveEmbeddings()
    }

    /** Find N closest terms in the vocab to the given vector, using only words from the in-set (if defined)
     * and excluding all words from the out-set (if non-empty).  Although you can, it doesn't make much
     * sense to define both in and out sets.
     * @param vector The vector.
     * @param inSet Set of words to consider. Specify None to use all words in the vocab (default behavior).
     * @param outSet Set of words to exclude (default to empty).
     * @param N The maximum number of terms to return (default to 40).
     * @return The N closest terms in the vocab to the given vector and their associated cosine similarity scores.
     */
    fun nearestNeighbours(
        vector: SimpleMatrix, inSet: Set<String>? = null,
        outSet: Set<String> = setOf(), N: Int = 40
    ): List<Pair<String, Double>> {
        val inputWords = (inSet ?: embeddings.keys) - outSet

        // TODO improve by smarter functions
        return embeddings
            .filterKeys(inputWords::contains)
            .map { (k, v) -> k to vector.cosineDistance(v) }
            .sortedBy { (_, cosineDistance) -> cosineDistance }
            .take(N)
    }

    /** Find the N closest terms in the vocab to the input word(s).
     * @param input The input word(s).
     * @param N The maximum number of terms to return (default to 40).
     * @return The N closest terms in the vocab to the input word(s) and their associated cosine similarity scores.
     */
    fun distance(input: List<String>, N: Int = 40): List<Pair<String, Double>> {
        val vectors = traverseVectors(input).reduce { acc, simpleMatrix -> acc + simpleMatrix }
        vectors.iDiv(vectors.fdrm.fastNormF()) // in-place

        return nearestNeighbours(vectors, outSet = input.toSet(), N = N)
    }

    /** Find the N closest terms in the vocab to the analogy:
     * - [w1] is to [w2] as [w3] is to ???
     *
     * The algorithm operates as follow:
     * - Find a vector approximation of the missing word = vec([w2]) - vec([w1]) + vec([w3]).
     * - Return words closest to the approximated vector.
     *
     * @param w1 First word in the analogy [w1] is to [w2] as [w3] is to ???.
     * @param w2 Second word in the analogy [w1] is to [w2] as [w3] is to ???
     * @param w3 Third word in the analogy [w1] is to [w2] as [w3] is to ???.
     * @param N The maximum number of terms to return (default to 40).
     *
     * @return The N closest terms in the vocab to the analogy and their associated cosine similarity scores.
     */
    fun analogy(w1: String, w2: String, w3: String, N: Int = 40): List<Pair<String, Double>>? =
        traverseVectors(listOf(w1, w2, w3))
            .takeIf { vectors -> vectors.size == 3 }
            ?.let { vec ->
                val vector = vec[1]
                vector.fdrm.minusAssign(vec[0].fdrm)
                vector.fdrm.plusAssign(vec[2].fdrm)
                vector.iDiv(vector.fdrm.fastNormF())

                nearestNeighbours(vector, outSet = setOf(w1, w2, w3), N = N)
            }

    /** Rank a set of words by their respective distance to some central term.
     * @param word The central word.
     * @param set Set of words to rank.
     * @return Ordered list of words and their associated scores.
     */
    fun rank(word: String, set: Set<String>): List<Pair<String, Double>> =
        vector(word)
            ?.let { vec -> nearestNeighbours(vec, inSet = set, N = set.size) }
            ?: emptyList()

    /** Pretty print the list of words and their associated scores.
     * @param words List of (word, score) pairs to be printed.
     */
    fun pprint(words: List<Pair<String, Double>>) {
        println("\n%50s${" ".repeat(7)}Cosine distance\n${"-".repeat(72)}".format("Word"))
        println(words.joinToString("\n") { (word, dist) -> "%50s${" ".repeat(7)}%15f".format(word, dist) })
    }

    companion object {
        fun fromFilePath(path: Path): WordEmbeddings {
            TODO("")
        }
    }
}