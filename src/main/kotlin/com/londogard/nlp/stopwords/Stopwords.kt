package com.londogard.nlp.stopwords

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport

/**
 * Returns stopwords for a country (LanguageSupport).
 * E.g. to retrieve Swedish stopwords use
 *  Stopwords.stopwords(sv)
 */
object Stopwords {
    fun stopwords(language: LanguageSupport): Set<String> =
        stopwordsOrNull(language) ?: throw IllegalArgumentException("There exists not stopwords for language ${language.name}. Please try again with one of the supported languages.")

    fun stopwordsOrNull(language: LanguageSupport): Set<String>? =
        if (language.hasStopwordSupport()) {
            DownloadHelper
                .getStopWords(language)
                .toFile()
                .readLines()
                .toSet()
        } else null
}