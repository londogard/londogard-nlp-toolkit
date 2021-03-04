package com.londogard.nlp.stemmer

import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.LanguageSupport.*
import org.tartarus.snowball.SnowballStemmer
import org.tartarus.snowball.ext.*

class Stemmer(language: LanguageSupport) {
    private val stemmer: SnowballStemmer = getStemmer(language)

    fun stem(word: String): String {
        stemmer.current = word
        stemmer.stem()
        return stemmer.current
    }

    companion object {
        // default to porterStemmer if unsupported Language
        fun getStemmer(language: LanguageSupport): SnowballStemmer = when (language) {
            sv -> swedishStemmer()
            nl -> dutchStemmer()
            en -> englishStemmer()
            fi -> finnishStemmer()
            fr -> frenchStemmer()
            de -> germanStemmer()
            hu -> hungarianStemmer()
            it -> italianStemmer()
            no -> norwegianStemmer()
            pt -> portugueseStemmer()
            ro -> romanianStemmer()
            ru -> russianStemmer()
            es -> spanishStemmer()
            tr -> turkishStemmer()
            else -> porterStemmer()
        }

        fun getPorterStemmer(): SnowballStemmer = porterStemmer()
    }
}