package com.londogard.nlp.keywords

import com.londogard.nlp.utils.LanguageSupport

interface Keywords<T: Number> {
    fun keywords(
        text: String,
        top: Int = 10,
        languageSupport: LanguageSupport = LanguageSupport.en
    ): List<Pair<List<String>, T>>
}