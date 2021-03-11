package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import com.londogard.nlp.utils.normalize
import org.ejml.simple.SimpleMatrix

class AverageSentenceEmbeddings(override val tokenEmbeddings: Embeddings): SentenceEmbeddings {
    override fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<SimpleMatrix> =
        listOfSentences.map(this::getSentenceEmbedding)

    override fun getSentenceEmbedding(sentence: List<String>): SimpleMatrix {
        return tokenEmbeddings
            .traverseVectors(sentence)
            .reduce { acc, simpleMatrix -> acc + simpleMatrix }
            .normalize()
    }
}