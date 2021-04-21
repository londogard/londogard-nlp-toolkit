package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import com.londogard.nlp.utils.*
import org.ejml.simple.SimpleMatrix
import kotlin.math.pow

/**
 * This implementation is based on the paper: _Unsupervised Random Walk Sentence Embeddings: A Strong but Simple Baseline_
 * (https://www.aclweb.org/anthology/W18-3012/)
 */
class USifSentenceEmbeddings(
    override val tokenEmbeddings: Embeddings,
    private val wordProb: Map<String, Float>,
    randomWalkLength: Int = 11, // = n, ~11
    private val numCommonDiscourseVector: Int = 5 // = m, 0 should work. In practise max 5.
) : SentenceEmbeddings {
    private val vocabSize = wordProb.size.toFloat()
    private val threshold = 1 - (1 - 1 / vocabSize).pow(randomWalkLength)
    private val alpha = wordProb.count { (_, prob) -> prob > threshold } / vocabSize
    private val Z = vocabSize / 2
    private val a = (1 - alpha) / (alpha * Z)
    private val defaultVector by lazy { SimpleMatrix(arrayOf(FloatArray(tokenEmbeddings.dimensions) { a })) }

    init {
        if (randomWalkLength < 0) throw IllegalArgumentException("randomWalkLength must be greater than 0 (was: $randomWalkLength)")
    }

    private fun weight(word: String): Double = a / (0.5 * a + wordProb.getOrDefault(word, 0f))

    override fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<SimpleMatrix> {
        val vectors = listOfSentences.map(this::getSentenceEmbedding)

        return if (numCommonDiscourseVector == 0) vectors
        else {
            val svd = vectors.first()
                .concatRows(*Array(vectors.size - 1) { i -> vectors[i + 1] })
                .svd(true)

            val singularValueSum = svd.singularValues.sumOf { it * it }

            (0 until numCommonDiscourseVector)
                .fold(vectors) { acc, i ->
                    val lambdaI = (svd.singularValues[i].pow(2) / singularValueSum).toFloat()
                    val pc = svd.v.getRow(i)
                    val pcTransposed = pc.transpose()
                    acc
                        .map { vector -> vector to pc.scale(vector.dot(pcTransposed)) }
                        .map { (vector, projection) -> vector - projection.iScale(lambdaI) }
                }
        }
    }

    override fun getSentenceEmbedding(sentence: List<String>): SimpleMatrix {
        val processedTokens = sentence.filter(tokenEmbeddings::contains)

        return if (processedTokens.isEmpty()) {
            defaultVector
        } else {
            tokenEmbeddings
                .traverseVectors(processedTokens)
                .map(SimpleMatrix::normalize)
                .mapIndexed { i, array -> array.iScale(weight(processedTokens[i]).toFloat()) }
                .reduce { acc, simpleMatrix ->
                    acc += simpleMatrix
                    acc
                }
                .apply { this /= processedTokens.size.toFloat() }
        }
    }
}