package com.londogard.nlp.embeddings.sentence

import com.londogard.nlp.embeddings.Embeddings

/** TODO
class TfIdfSentenceEmbeddings(override val tokenEmbeddings: Embeddings) : SentenceEmbeddings {
    private lateinit var tfidfMap: Map<String, Float>
    private val default: Float = 0.0f

    fun train(sentences: List<String>, threshold: Double = 0.0, k: Int = Int.MAX_VALUE) {
        val corpus = sentences.map { it.bag(stemmer = null) }
        val words = corpus.flatMap { bag -> bag.keys }.distinct()
        val bags = corpus.map { vectorize(words.toTypedArray(), it) }
        val vectors = tfidf(bags)
        val vector = Matrix.of(vectors.toTypedArray()).colSums()
        val vecMax = vector.max() ?: 1.0

        tfidfMap = vector
            .map { it / vecMax }
            .mapIndexedNotNull { idx, rel -> if (rel > threshold) words[idx] to rel.toFloat() else null }
            .let { relMap -> if (k < Int.MAX_VALUE) relMap.sortedByDescending { it.second }.take(k) else relMap }
            .toMap()
    }

    fun loadPretrained(pretrained: Map<String, Float>) { tfidfMap = pretrained }

    override fun getSentenceEmbedding(tokens: List<String>): Array<Float> {
        if (!this::tfidfMap.isInitialized) throw Error("Not trained!")
        return tokens
            .mapNotNull { word -> embeddings.vector(word)?.mMul(tfidfMap.getOrDefault(word, default)) }
            .sumByColumns()
            .normalize()
    }
}
*/