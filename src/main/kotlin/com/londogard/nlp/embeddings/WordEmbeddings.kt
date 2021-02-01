package com.londogard.nlp.embeddings

import org.apache.commons.math3.linear.SingularValueDecomposition
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.executioner.DefaultOpExecutioner
import org.nd4j.linalg.api.ops.impl.reduce.custom.BatchMmul
import org.nd4j.linalg.api.ops.impl.reduce3.CosineDistance
import org.nd4j.linalg.api.ops.impl.reduce3.CosineSimilarity
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import smile.sequence.CRF
import org.deeplearning4j.models.word2vec.Word2Vec
import org.nd4j.linalg.api.ops.impl.transforms.custom.Svd
import smile.math.matrix.FloatMatrix

abstract class WordEmbeddings(
//    override val dimensions: Int = DownloadHelper.dimension,
//    override val filename: String = DownloadHelper.embeddingPath,
    override val delimiter: Char = ' ',
    override val normalized: Boolean = true
) : Embeddings() {
    /** Vocabulary, word to embedded space */
//    override val (embeddings, ndArray) by lazy { loadEmbeddingsFromFile() }

    init {

//        if (filename == DownloadHelper.embeddingPath && !DownloadHelper.embeddingsExist())
//            DownloadHelper.downloadGloveEmbeddings()
    }

    object a {
        @JvmStatic
        fun main(args: Array<String>) {

            val b = Nd4j.createFromArray(arrayOf(
                arrayOf(1f,1f,1f),
                arrayOf(2f,2f,2f),
                arrayOf(3f,3f,3f),
            ))
            val a = Nd4j.createFromArray(arrayOf(1f, 2f, 3f))
            val array = Nd4j.zeros(DataType.BFLOAT16, b.rows().toLong(), b.rows().toLong())
            (0 until  b.rows()).forEach { i ->
                (0 until b.columns()).forEach { j ->
                    when {
                        i == j -> array.put(i, j, 0)
                        j < i -> array.put(i, j, array.getDouble(j, i))
                        else -> array.put(i, j, Transforms.cosineDistance(b.getRow(i.toLong()), b.getColumn(j.toLong())))
                    }
                }
            }
            println(array)
        }
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
        vector: INDArray, inSet: Set<String>? = null,
        outSet: Set<String> = setOf(), N: Int = 40
    ): List<Pair<String, Double>> {
        if (inSet == null) {
            Transforms.allCosineDistances(Nd4j.ones(3), Nd4j.ones(3, 3))
        } else {

        }
        val inputWords = (inSet ?: embeddings.keys) - outSet
        val (keys, indices) = embeddings.filterKeys(inputWords::contains).toList().unzip()
//        return
//            .map { (k, v) -> k to SimpleDistances.cosine(vector, v) }
//            .sortedByDescending { (_, cosineDist) -> cosineDist }
//            .take(N)
        TODO("")
    }

    /** Find the N closest terms in the vocab to the input word(s).
     * @param input The input word(s).
     * @param N The maximum number of terms to return (default to 40).
     * @return The N closest terms in the vocab to the input word(s) and their associated cosine similarity scores.
     */
    fun distance(input: List<String>, N: Int = 40): List<Pair<String, Double>>? = TODO("")
//        traverseVectors(input)
//            ?.let { vectors -> nearestNeighbours(vectors.sumByColumns().normalize(), outSet = input.toSet(), N = N) }

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
    fun analogy(w1: String, w2: String, w3: String, N: Int = 40): List<Pair<String, Double>>? = TODO("")
//        traverseVectors(listOf(w1, w2, w3))
//            ?.takeIf { it.size == 3 }
//            ?.let { vec ->
//                val vector = (vec[1] `--` vec[0]) `++` vec[2]
//                nearestNeighbours(vector.normalize(), outSet = setOf(w1, w2, w3), N = N)
//            }

    /** Rank a set of words by their respective distance to some central term.
     * @param word The central word.
     * @param set Set of words to rank.
     * @return Ordered list of words and their associated scores.
     */
    fun rank(word: String, set: Set<String>): List<Pair<String, Double>> =
        vector(word)
            ?.let { vec -> nearestNeighbours(vec, inSet = set, N = set.size) }
            ?: listOf()

    /** Pretty print the list of words and their associated scores.
     * @param words List of (word, score) pairs to be printed.
     */
    fun pprint(words: List<Pair<String, Double>>) {
        println("\n%50s${" ".repeat(7)}Cosine distance\n${"-".repeat(72)}".format("Word"))
        println(words.joinToString("\n") { (word, dist) -> "%50s${" ".repeat(7)}%15f".format(word, dist) })
    }
}