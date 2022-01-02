package com.londogard.nlp.langid

import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetector
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder

// Based on Lingua
class LanguageIdentifier {
    val detector: LanguageDetector = LanguageDetectorBuilder.fromLanguages(Language.ENGLISH,
        Language.FRENCH, Language.GERMAN, Language.SPANISH
    ).build()
    val detectedLanguage: Language = detector.detectLanguageOf(text = "languages are awesome")
}