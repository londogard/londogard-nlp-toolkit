package com.londogard.nlp.wordfreq

import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.LanguageSupport.*

enum class WordFrequencySize {
    Largest, Smallest;

    private fun stringify(languageSupport: LanguageSupport): String = when(languageSupport) {
        nb -> no.toString()
        ba, hr, rs, cs, me -> sh.toString()
        else -> languageSupport.toString()
    }

    // no only exists as nb=norwegian bokmål
    internal fun toFileName(languageSupport: LanguageSupport): String = when (this) {
        Largest -> "${languageSupport.largestWordFrequency()}_${stringify(languageSupport)}.gz"
        Smallest -> "small_${stringify(languageSupport)}.gz"
    }
}