package com.londogard.nlp.stopwords

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.readLines

/**
 * Returns stopwords for a country (LanguageSupport).
 * E.g. to retrieve Swedish stopwords use
 *  Stopwords.stopwords(sv)
 */
object Stopwords {
    var cache: Pair<LanguageSupport, Set<String>>? = null

    fun isStopword(word: String, language: LanguageSupport): Boolean =
        stopwordsOrNull(language)?.contains(word) == true

    @Throws(IllegalArgumentException::class)
    fun stopwords(language: LanguageSupport): Set<String> =
        stopwordsOrNull(language)
            ?: throw IllegalArgumentException("There exists not stopwords for language ${language.name}. Please try again with one of the supported languages.")

    fun stopwordsOrNull(language: LanguageSupport): Set<String>? {
        val cacheStopwords = cache

        return when {
            cacheStopwords?.first == language -> cacheStopwords.second
            language.hasStopWordSupport() -> {
                val stopwords = DownloadHelper.getStopWords(language).readLines().toSet()
                cache = language to stopwords

                stopwords
            }
            else -> null
        }
    }
}