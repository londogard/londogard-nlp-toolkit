package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

interface SentenceEmbeddings {
    val tokenEmbeddings: Embeddings
    fun getSentenceEmbeddings(listOfSentences: List<List<String>>): List<D1Array<Float>>
    fun getSentenceEmbedding(sentence: List<String>): D1Array<Float>
}