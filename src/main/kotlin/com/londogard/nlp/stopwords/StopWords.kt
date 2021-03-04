package com.londogard.nlp.stopwords

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.readLines

/**
 * Returns stopwords for a country (LanguageSupport).
 * E.g. to retrieve Swedish stopwords use
 *  Stopwords.stopwords(sv)
 */
object StopWords {
    var cache: Pair<LanguageSupport, Set<String>>? = null

    fun isStopWord(word: String, language: LanguageSupport): Boolean =
        stopwordsOrNull(language)?.contains(word) == true

    fun stopWords(language: LanguageSupport): Set<String> =
        stopwordsOrNull(language)
            ?: throw IllegalArgumentException("There exists not stopwords for language ${language.name}. Please try again with one of the supported languages.")

    fun stopwordsOrNull(language: LanguageSupport): Set<String>? {
        val cacheStopWords = cache

        return when {
            cacheStopWords?.first == language -> cacheStopWords.second
            language.hasStopWordSupport() -> {
                val stopWords = DownloadHelper.getStopWords(language).readLines().toSet()
                cache = language to stopWords

                stopWords
            }
            else -> null
        }
    }
}