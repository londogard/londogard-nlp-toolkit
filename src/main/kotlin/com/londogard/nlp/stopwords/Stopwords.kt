package com.londogard.nlp.stopwords

import com.londogard.nlp.utils.LanguageSupport
import java.util.*

object Stopwords {
    fun stopwords(language: LanguageSupport): Set<String>? =
        if (language.hasStopwordSupport()) {

            TODO("")
        } else null
}