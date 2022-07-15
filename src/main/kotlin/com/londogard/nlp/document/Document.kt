package com.londogard.nlp.document

import com.londogard.nlp.tokenizer.Tokenizer


data class Classification<T>(val classification: T, val probability: Float)

interface DocumentClassifier // Categories
interface TokenClassifier // Lemma, PoS, NER, etc
interface Grapher
interface Entity

interface Token {
    val entity: Entity
    val lemma: String
    val PoS: String
    val dependencies: List<String>

}
interface TokenTransformer

class Document(
    val tokenizer: Tokenizer,
    val entityTagger: TokenClassifier? = null,
    val classifer: DocumentClassifier? = null,
    val partOfSpeechTagger: TokenClassifier? = null,
    val lemmatizer: TokenTransformer? = null,
    val dependencyTagger: Any
) {
    val tokens: List<Token> = TODO()


}