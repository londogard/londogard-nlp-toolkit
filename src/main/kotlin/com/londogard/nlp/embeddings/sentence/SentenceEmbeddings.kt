package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import org.ejml.simple.SimpleMatrix

interface SentenceEmbeddings {
    val tokenEmbeddings: Embeddings
    fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<SimpleMatrix>
    fun getSentenceEmbedding(sentence: List<String>): SimpleMatrix
}