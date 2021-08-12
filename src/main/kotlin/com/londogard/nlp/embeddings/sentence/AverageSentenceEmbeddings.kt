package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import com.londogard.nlp.utils.avgNorm
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

class AverageSentenceEmbeddings(override val tokenEmbeddings: Embeddings): SentenceEmbeddings {
    override fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<D1Array<Float>> =
        listOfSentences.map(this::getSentenceEmbedding)

    override fun getSentenceEmbedding(sentence: List<String>): D1Array<Float> {
        return tokenEmbeddings
            .traverseVectors(sentence)
            .avgNorm()
    }
}