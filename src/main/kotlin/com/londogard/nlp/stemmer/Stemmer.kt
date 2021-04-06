package com.londogard.nlp.stemmer

import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.LanguageSupport.*
import org.tartarus.snowball.SnowballStemmer
import org.tartarus.snowball.ext.*

// default to porterStemmer if unsupported Language
class Stemmer(language: LanguageSupport) {
    private val stemmer: SnowballStemmer = getStemmer(language)

    fun stem(word: String): String {
        stemmer.current = word
        stemmer.stem()
        return stemmer.current
    }

    private fun getStemmer(language: LanguageSupport): SnowballStemmer = when (language) {
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

    companion object {
        var cache: Pair<LanguageSupport, Stemmer>? = null

        // Default to PorterStemmer if not supported!
        @JvmStatic fun stem(word: String, language: LanguageSupport): String {
            val cachedStemmer = cache

            return when (cachedStemmer?.first) {
                language -> cachedStemmer.second.stem(word)
                else -> {
                    val stemmer = Stemmer(language)
                    cache = language to stemmer

                    stemmer.stem(word)
                }
            }
        }
    }
}