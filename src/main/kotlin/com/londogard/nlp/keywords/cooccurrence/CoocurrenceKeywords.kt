package com.londogard.nlp.keywords.cooccurrence

import com.londogard.nlp.stemmer.Stemmer
import com.londogard.nlp.stopwords.Stopwords
import com.londogard.nlp.tokenizer.SimpleTokenizer
import com.londogard.nlp.tokenizer.Tokenizer
import com.londogard.nlp.tokenizer.sentence.SimpleSentenceTokenizer
import com.londogard.nlp.utils.LanguageSupport
import org.jetbrains.dataframe.impl.asList
import java.awt.SystemColor
import java.util.*


object CoocurrenceKeywords {
    private val punctuations = Regex("\\p{Punct}")

    fun keywords(
        text: String,
        top: Int = 10,
        languageSupport: LanguageSupport = LanguageSupport.en
    ): Set<List<String>> {
        val tokenizer = SimpleTokenizer()
        val stemmer = Stemmer(languageSupport)

        // Split text into sentences. Stem words by Porter algorithm.
        var ntotal = 0
        val sentences = SimpleSentenceTokenizer()
            .split(text)
            .fold(listOf<List<String>>()) { acc, sentence ->
                val a = tokenizer.split(sentence)
                (acc + a) as List<List<String>> // strip pluralis
            }
        val numSentences = sentences.size

        //  Extract phrases by Apriori-like algorithm.
        val allNgrams = toNgramSortedByFreq(sentences.flatten())

//        // Select upto 30% most frequent terms.
        val n = 3 * allNgrams.size / 10
        val freqTerms = allNgrams.take(n)
//        val freqTerms: Array<NGram> = arrayOfNulls<NGram>(n)
//        {
//            var i = 0
//            val start = terms.size - n
//            while (i < n) {
//                freqTerms.get(i) = terms[start + i]
//                i++
//            }
//        }
//
//        // Trie for phrase matching.
//
//        // Trie for phrase matching.
//        val trie: Trie<String, Int> = Trie()
//        for (i in 0 until n) {
//            trie.put(freqTerms[i].words, i)
//        }
//
//        // Build co-occurrence table
//
//        // Build co-occurrence table
//        val nw = IntArray(n)
//        val table = Array(n) { IntArray(n) }
//        for (sentence in sentences) {
//            val phrases: MutableSet<Int> = HashSet()
//            for (j in 1..maxNGramSize) {
//                for (i in 0..sentence.size - j) {
//                    val phrase = Arrays.copyOfRange(sentence, i, i + j)
//                    val index: Int = trie.get(phrase)
//                    if (index != null) {
//                        phrases.add(index)
//                    }
//                }
//            }
//            for (i in phrases) {
//                nw[i] += phrases.size
//                for (j in phrases) {
//                    if (i != j) {
//                        table[i][j]++
//                    }
//                }
//            }
//        }
//
//        // Clustering frequent terms.
//
//        // Clustering frequent terms.
//        val cluster = IntArray(n)
//        for (i in cluster.indices) {
//            cluster[i] = i
//        }
//
//        //double log2 = Math.log(2.0);
//
//        //double log2 = Math.log(2.0);
//        for (i in 0 until n) {
//            for (j in i + 1 until n) {
//                // Mutual information
//                if (table[i][j] > 0) {
//                    // This doesn't work as ntotal is usually large and thus the mutual information
//                    // is way larger than the threshold log2 given in the paper.
//                    //double mutual = Math.log((double) ntotal * table[i][j] / (freqTerms[i].freq * freqTerms[j].freq));
//                    // Here we just use the (squared) geometric average of co-occurrence probability
//                    // It works well to clustering things like "digital computer" and "computer" in practice.
//                    val mutual: Double =
//                        table[i][j].toDouble() * table[i][j] / (freqTerms[i].count * freqTerms[j].count)
//                    if (mutual >= 0.25) {
//                        cluster[j] = cluster[i]
//                    } /*else {
//                        double js = 0.0; // Jsensen-Shannon divergence
//                        for (int k = 0; k < n; k++) {
//                            double p1 = (double) table[i][k] / freqTerms[i].freq;
//                            double p2 = (double) table[j][k] / freqTerms[j].freq;
//                            // The formula in the paper is not correct as p is not real probablity.
//                            if (p1 > 0 && p2 > 0) {
//                                js += -(p1+p2) * Math.log((p1+p2)/2.0) + p1 * Math.log(p1) + p2 * Math.log(p2);
//                            }
//                        }
//
//                        js /= 2.0;
//                        if (js > log2) {
//                            cluster[j] = cluster[i];
//                        }
//                    }*/
//                }
//            }
//        }
//
//        // Calculate expected probability
//
//        // Calculate expected probability
//        val pc = DoubleArray(n)
//        for (i in 0 until n) {
//            for (j in 0 until n) {
//                pc[cluster[j]] += table[i][j]
//            }
//        }
//        for (i in 0 until n) {
//            pc[i] /= ntotal
//        }
//
//
//        // Calculate chi-square scores.
//
//
//        // Calculate chi-square scores.
//        val score = DoubleArray(n)
//        for (i in 0 until n) {
//            var max = Double.NEGATIVE_INFINITY
//            for (j in 0 until n) {
//                if (cluster[j] != j) {
//                    continue
//                }
//                var fwc = 0.0
//                for (k in 0 until n) {
//                    if (cluster[k] == j) fwc += table[i][k]
//                }
//                val expected = nw[i] * pc[j]
//                val d = fwc - expected
//                val chisq = d * d / expected
//                score[i] += chisq
//                if (chisq > max) max = chisq
//            }
//            //score[i] -= max;
//        }
//
//        val index: IntArray = QuickSort.sort(score)
//        val keywords: ArrayList<NGram> = ArrayList<NGram>()
//        {
//            var i = n
//            while (i-- > 0) {
//                var add = true
//                // filter out components of phrases, e.g. "digital" in "digital computer".
//                for (j in i + 1 until n) {
//                    if (cluster[index[j]] == cluster[index[i]]) {
//                        if (freqTerms[index[j]].words.length >= freqTerms[index[i]].words.length) {
//                            add = false
//                            break
//                        } else {
//                            keywords.remove(freqTerms[index[j]])
//                            add = true
//                        }
//                    }
//                }
//                if (add) {
//                    keywords.add(freqTerms[index[i]])
//                    if (keywords.size >= maxNumKeywords) break
//                }
//            }
//        }
//
//        return keywords.toArray(arrayOfNulls<NGram>(0))
        TODO()
    }

    fun toNgramSortedByFreq(tokens: List<String>, n: Int = 4, minFreq: Int = 4): List<List<String>> {
        val stopwords = Stopwords.stopwords(LanguageSupport.en)
        return (1..n)
            .fold(emptyMap<List<String>, Int>()) { acc, ngram ->
                val ngrams = when (ngram) {
                    1 -> tokens.filterNot(punctuations::matches).map { listOf(it) }
                    else -> tokens.windowed(ngram)
                }
                acc + ngrams.groupingBy { it }.eachCount().filterValues { count -> count > minFreq }
            }
            .entries
            .sortedByDescending { (_, count) -> count }
            .map { (key, _) -> key }
            .filter { ngram ->
                when (ngram.size) {
                    1, 2 -> ngram.none(stopwords::contains)
                    else -> !stopwords.contains(ngram.first()) && !stopwords.contains(ngram.last()) &&
                            ngram.any { !stopwords.contains(it) }
                }
            }
            .asList()
    }
}