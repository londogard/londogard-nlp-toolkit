package com.londogard.nlp.keywords

import com.londogard.nlp.stemmer.Stemmer
import com.londogard.nlp.stopwords.Stopwords
import com.londogard.nlp.structures.trie.TrieNode
import com.londogard.nlp.tokenizer.SimpleTokenizer
import com.londogard.nlp.tokenizer.sentence.SimpleSentenceTokenizer
import com.londogard.nlp.utils.LanguageSupport
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.divAssign
import kotlin.math.min
import kotlin.math.pow

/**
 * Unsupervised Keyword Extraction
 * Based on  algorithm proposed in [DOI:10.1142/S0218213004001466](https://www.researchgate.net/publication/2572200_Keyword_Extraction_from_a_Single_Document_using_Word_Co-occurrence_Statistical_Information)
 */
object CooccurrenceKeywords: Keywords<Int> {
    private val punctuations = Regex("\\p{Punct}+")

    override fun keywords(
        text: String,
        top: Int,
        languageSupport: LanguageSupport
    ): List<Pair<List<String>, Int>> {
        val tokenizer = SimpleTokenizer()
        val stemmer = Stemmer(languageSupport)

        // Split text into sentences. Stem words.
        val sentences = SimpleSentenceTokenizer()
            .split(text)
            .fold(listOf<List<String>>()) { acc, sentence ->
                val tokens = tokenizer
                    .split(sentence)
                    .map(String::lowercase)
                    .map(stemmer::stem) // full stem compared to
                    .filterNot(String::isBlank)

                acc.plusElement(tokens)
            }
            .filter(List<String>::isNotEmpty)

        val numWords = sentences.sumOf { it.size }

        //  Extract n-grams
        val maxNgramSize = 4
        val allNgrams = toNgramSortedByFreq(sentences.flatten(), n = maxNgramSize, minFreq = min(4, sentences.size / 3))

        // Select 30% most frequent terms
        val n = (3 * allNgrams.size / 10).coerceAtLeast(1)
        val freqTerms = allNgrams.take(n).asReversed()

        // Trie for phrase matching.
        val trie = TrieNode.ofData(freqTerms.map { it.first }.zip(freqTerms.indices))

        // Build co-occurrence table
        val nw = mk.d1array(n) { 0 }
        val table = mk.d2array(n, n) { 0 }

        sentences.forEach { sentence ->
            val phrases = mutableSetOf<Int>()

            for (j in 1..maxNgramSize) {
                for (i in 0..(sentence.size - j)) {
                    val phrase = sentence.subList(i, i + j)

                    trie.get(phrase)?.let(phrases::add)
                }
            }

            phrases.forEach { i ->
                nw[i] += phrases.size
                phrases.forEach { j ->
                    if (i != j) { table[i, j] += 1 }
                }
            }
        }

        // Clustering frequent terms.
        val cluster = mk.d1array(n) { i -> i }

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (table[i, j] > 0) {
                    // If we co-exist with a term a lot of time we will be the "same".
                    val mutual: Double =
                        table[i, j].toDouble().pow(2) / (freqTerms[i].second * freqTerms[j].second)

                    if (mutual >= 0.25) {
                        cluster[j] = cluster[i]
                    }
                }
            }
        }

        // Calculate expected probability
        val pc = mk.d1array(n) { 0.0 }
        for (i in 0 until n) {
            for (j in 0 until n) {
                pc[cluster[j]] += table[i, j].toDouble()
            }
        }

        pc /= numWords.toDouble()

        // Calculate chi-square scores.
        val score = mk.d1array(n) { 0.0 }
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (cluster[j] != j) { continue }

                var fwc = 0.0
                for (k in 0 until n) {
                    if (cluster[k] == j) fwc += table[i][k]
                }

                val expected = nw[i] * pc[j]
                val d = fwc - expected
                val chisq = d.pow(2) / expected
                score[i] += chisq
            }
        }

        val index = IntArray(score.size) { i -> i }.sortedBy(score::get)
        val keywords = mutableListOf<Pair<List<String>, Int>>()

        for (i in (n - 1) downTo 0) {
            var add = true
            // filter out components of phrases, e.g. "digital" in "digital computer".
            for (j in (i + 1) until n) {
                if (cluster[index[j]] == cluster[index[i]]) {
                    if (freqTerms[index[j]].first.size >= freqTerms[index[i]].first.size) {
                        add = false
                        break
                    } else {
                        keywords.remove(freqTerms[index[j]])
                        add = true
                    }
                }
            }
            if (add) {
                keywords.add(freqTerms[index[i]])
                if (keywords.size >= top) break
            }
        }

        return keywords.toList()
    }

    private fun toNgramSortedByFreq(tokens: List<String>, n: Int = 4, minFreq: Int = 4): List<Pair<List<String>, Int>> {
        val stopwords = Stopwords.stopwords(LanguageSupport.en)

        return (1..n)
            .fold(emptyMap<List<String>, Int>()) { acc, ngram ->
                val ngrams = when (ngram) {
                    1 -> tokens.filterNot(punctuations::matches).map { listOf(it) }
                    else -> tokens.windowed(ngram)
                }
                acc + ngrams.groupingBy { it }.eachCount()
                    .filterValues { count -> count > minFreq }
            }
            .asIterable()
            .sortedByDescending { (_, count) -> count }
            .filter { (ngram, _) ->
                when (ngram.size) {
                    1, 2 -> ngram.none(stopwords::contains)
                    else -> !stopwords.contains(ngram.first())
                            && !stopwords.contains(ngram.last())
                            && !ngram.subList(1, ngram.size - 1).all(stopwords::contains)
                }
            }
            .map { it.toPair() }
    }
}