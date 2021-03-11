package com.londogard.nlp.information

import com.londogard.nlp.utils.LanguageSupport
import org.ejml.simple.SimpleMatrix

object Information {
    fun <T> bagOfWords(words: List<T>): Map<T, Int> {
        return words.groupingBy { it }.eachCount()
    }

    fun <T> vectorize(features: List<T>, bag: Map<T, Int>): DoubleArray {
        return DoubleArray(features.size) { i -> bag.getOrDefault(features[i], 0).toDouble() }
    }

    fun vectorize(tokens: List<String>, bag: Map<String, Int>): List<Int> {
        return tokens.map { token -> bag.getOrDefault(token, 0) }
    }

    fun <T> tfidf(documents: List<List<T>>): Map<T, Float> {
        val bags = documents
        TODO("")
    }

    fun TfidfVectorizer(
        data: List<List<Int>>,
        ngram_range: Pair<Int, Int> = Pair(1,1),
        maxDf: Double = 1.0,
        minDf: Double = 1.0,
        maxFeatures: Int? = null,
        vocab: Set<String>? = null
    ) {
        TODO("")
    }



    /**
     * TfidfVectorizer(*, input='content', encoding='utf-8', decode_error='strict',
     * strip_accents=None, lowercase=True, preprocessor=None, tokenizer=None,
     * analyzer='word', stop_words=None, token_pattern='(?u)\b\w\w+\b', ngram_range=(1, 1),
     * +max_df=1.0, min_df=1, max_features=None, vocabulary=None, binary=False,
     * dtype=<class 'numpy.float64'>, norm='l2', use_idf=True, smooth_idf=True,
     * sublinear_tf=False)[source]
     */
}