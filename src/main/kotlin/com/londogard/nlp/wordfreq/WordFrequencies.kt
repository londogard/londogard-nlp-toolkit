package com.londogard.nlp.wordfreq

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.math.log10
import kotlin.math.pow

/**
 * Returns word frequencies for a country (LanguageSupport).
 * E.g. to retrieve Swedish word frequencies use
 *  WordFrequencies.wordFrequency("hej", sv)
 */
object WordFrequencies {
    private var cache: Pair<Path, Map<String, Float>>? = null

    fun getAllWordFrequenciesOrNull(
        language: LanguageSupport,
        size: WordFrequencySize = WordFrequencySize.Largest
    ): Map<String, Float>? =
        if (language.hasWordFrequencySupport()) {
            val wordFrequencyPath = DownloadHelper.getWordFrequencies(language, size)
            unpackFile(wordFrequencyPath)
        } else null

    fun getAllZipfFrequenciesOrNull(language: LanguageSupport, size: WordFrequencySize): Map<String, Float>? =
        if (language.hasWordFrequencySupport()) {
            val wordFrequencyPath = DownloadHelper.getWordFrequencies(language, size)
            unpackFile(wordFrequencyPath).mapValues { (_, frequency) -> frequencyToZipf(frequency) }
        } else null

    fun wordFrequency(
        word: String,
        language: LanguageSupport,
        minimum: Float = 0f,
        size: WordFrequencySize = WordFrequencySize.Smallest
    ): Float =
        if (language.hasWordFrequencySupport()) {
            wordFrequencyOrNull(word, language, size) ?: minimum
        } else throw IllegalArgumentException("There exists not word frequency for language ${language.name} with size ${size.name}. Please try again with one of the supported language/size combinations.")

    fun wordFrequencyOrNull(
        word: String,
        language: LanguageSupport,
        size: WordFrequencySize = WordFrequencySize.Smallest
    ): Float? =
        if (language.hasWordFrequencySupport()) {
            val wordFrequencyPath = DownloadHelper.getWordFrequencies(language, size)
            val wordFrequencies = unpackFile(wordFrequencyPath)

            wordFrequencies[word.toLowerCase()]
        } else null

    fun zipfFrequencyOrNull(
        word: String,
        language: LanguageSupport,
        size: WordFrequencySize = WordFrequencySize.Smallest
    ): Float? =
        if (language.hasWordFrequencySupport()) {
            val wordFrequencyPath = DownloadHelper.getWordFrequencies(language, size)
            val wordFrequencies = unpackFile(wordFrequencyPath)
            wordFrequencies[word.toLowerCase()]?.let(this::frequencyToZipf)
        } else null

    fun zipfFrequency(
        word: String,
        language: LanguageSupport,
        minimum: Float = 0f,
        size: WordFrequencySize = WordFrequencySize.Smallest
    ): Float =
        if (language.hasWordFrequencySupport()) {
            zipfFrequencyOrNull(word, language, size) ?: minimum
        } else throw IllegalArgumentException("There exists not word frequency for language ${language.name} with size ${size.name}. Please try again with one of the supported language/size combinations.")

    private fun frequencyToZipf(frequency: Float): Float = log10(frequency) + 9

    // TODO exchange for cache pattern in stopwords/stemmer
    private fun unpackFile(path: Path): Map<String, Float> {
        val unpackedFile by lazy {
            path.toFile()
                .inputStream()
                .let(::GZIPInputStream)
                .let(GZIPInputStream::bufferedReader)
                .useLines { lines ->
                    lines
                        .mapIndexed { index, line ->
                            10f.pow(-index.toFloat() / 100) to line.split(' ').filterNot(String::isEmpty).toHashSet()
                        }
                        .filter { (_, tokens) -> tokens.isNotEmpty() }
                        .flatMap { (prob, tokens) -> tokens.map { it to prob } }
                        .toMap()
                }
        }

        return if (cache?.first == path) cache?.second ?: unpackedFile
        else {
            cache = path to unpackedFile
            unpackedFile
        }
    }
}