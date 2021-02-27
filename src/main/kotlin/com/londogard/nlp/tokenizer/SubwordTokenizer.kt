package com.londogard.nlp.tokenizer

import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.wordfreq.WordFrequencies
import com.londogard.nlp.wordfreq.WordFrequencySize
import kotlin.math.roundToInt

/**
 * Londogard specific implementation that's based on a shared idea from Unigram Tokenizer & BPE Tokenizer
 *  1. Create Vocab, keep ngram (n=X)
 *  2. Merge top % tokens (Y%) into ngram, keeping (n-1)gram too
 *  3. Repeat until good size
 * This allows ...
 *
 * Unlike BPE we batch the operations which results in a faster training process albeit the result should keep pretty intact.
 */

// toByte or toInt
// vocab can be our WordFreq which simplifies life for users

// 28k words in Sweden (small)
// 320k words in English (large)

// 1. Split into unigram probs
// 2. Always save unigram
// 3. Max Xgram
// 4. Use shorts (64k vocab = max)
// 5. Max Y sized vocab
// 6. Allow extensibility by custom pre-processing
// 7. 

class SubwordTokenizer(val maxVocabSize: Short, vocab: Map<String, Int>) : Tokenizer {

    init {
        val unigramSet = vocab
            .keys
            .flatMap { word -> word.toSet() }
        //val unigramProb = unigramSet.map { char -> char to 0 }.let { pairs -> pairs.toMap(HashMap(pairs.size)) }
        //vocab.forEach { t, u -> t.forEach {  } }
        //
        val unigramProb = vocab
            .flatMap { (key, count) -> key.map { char -> char to count } }
            .groupingBy { (key,_) -> key }
            .fold(0) { acc, (_, count) -> acc + count }

        // todo add fastAI also

            //.aggregate { key, accumulator: Int, element, first -> (accumulator ?: 0) + element.secnd}

                TODO("")
    }

    override fun split(text: String): List<String> {
        TODO("Not yet implemented")
    }

    // Should be supplied serialized
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val wordFreq = WordFrequencies.getAllWordFrequenciesOrNull(LanguageSupport.en, WordFrequencySize.Largest)
            println(wordFreq?.size)
        }

        // TODO
        //      - Vocab Size
        //      - Vocab % reduction
        fun fromLanguage(maxVocabSize: Short, language: LanguageSupport): SubwordTokenizer {
            val wordFreq = WordFrequencies.getAllWordFrequenciesOrNull(language, WordFrequencySize.Largest)
            println(wordFreq?.size)
            val min = wordFreq?.values?.minOrNull() ?: throw IllegalArgumentException("Lang $language not supported")
            val scale = (1 / min).roundToInt()
            val vocab = wordFreq.mapValues { (_, value) -> (value * scale).roundToInt() }

            return SubwordTokenizer(maxVocabSize, vocab)
        }
    }
}