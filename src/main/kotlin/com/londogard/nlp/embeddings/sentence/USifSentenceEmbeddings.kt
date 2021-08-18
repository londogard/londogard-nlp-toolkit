package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.utils.getRow
import com.londogard.nlp.utils.norm2
import org.ejml.simple.SimpleMatrix
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.operations.*
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
    private val defaultVector by lazy { mk.d1array(tokenEmbeddings.dimensions) { a } }

    init {
        if (randomWalkLength < 0) throw IllegalArgumentException("randomWalkLength must be greater than 0 (was: $randomWalkLength)")
    }

    private fun weight(word: String): Double = a / (0.5 * a + wordProb.getOrDefault(word, 0f))

    override fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<D1Array<Float>> {
        val vectors = listOfSentences.map(this::getSentenceEmbedding)

        return if (numCommonDiscourseVector == 0) vectors
        else {

            val svd = SimpleMatrix(vectors.map { it.data.getFloatArray() }.toTypedArray()).svd(true)
            // TODO remove Simple API in favour of only Float
            // svd = DecompositionFactory_FDRM.svd(m.numRows, m.numCols, true, true, compact);
            val singularValueSum = svd.singularValues.sumOf { it * it }

            (0 until numCommonDiscourseVector)
                .fold(vectors) { acc, i ->
                    val lambdaI = (svd.singularValues[i].pow(2) / singularValueSum).toFloat()
                    val pc = svd.v.getRow(i)
                    val pcD1 = mk.d1array(pc.numElements) { pc[it].toFloat() }
                    val pcTransposed = pcD1.transpose()

                    // TODO simplify by projection extraction (timesAssign at end is not project)
                    acc
                        .map { vector -> vector - (pcD1 * (mk.linalg.dot(vector, pcTransposed))).apply { timesAssign(lambdaI) } }
                }
        }
    }

    override fun getSentenceEmbedding(sentence: List<String>): D1Array<Float> {
        val processedTokens = sentence.filter(tokenEmbeddings::contains)

        return if (processedTokens.isEmpty()) {
            defaultVector
        } else {
            tokenEmbeddings
                .traverseVectors(processedTokens)
                .map { it / it.norm2() }
                .mapIndexed { i, array -> array.inplaceOp { it * weight(processedTokens[i]).toFloat() } as D1Array<Float> }
                .reduce { acc, d1Array ->
                    acc += d1Array
                    acc
                }
                .apply { this /= processedTokens.size.toFloat() }
        }
    }
}