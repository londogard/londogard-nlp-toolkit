package com.londogard.nlp.wordfreq

import com.londogard.nlp.utils.LanguageSupport

enum class WordFrequencySize {
    Largest, Smallest;

    internal fun toFileName(languageSupport: LanguageSupport): String = when (this) {
        Largest -> "${languageSupport.largestWordFrequency()}_${languageSupport.name}.gz"
        Smallest -> "small_${languageSupport.name}.gz"
    }
}