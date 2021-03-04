package com.londogard.nlp.utils

/**
 * All languages and their support.
 * For conversion from ISO-code, see https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
 */
enum class LanguageSupport {
    ar, cs, de, en, es, fi, fr, it, ja, nl, pl, pt, ru, zh,
    bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk,
    ms, nb, ro, sh, sv, tr, uk, az, kk, ne, no, sl, tg;

    fun hasStemmer(): Boolean = when(this) {
        sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es, tr -> true
        else -> false
    }

    fun hasWordEmbeddings(): Boolean = when(this) {
        else -> false
    }

    fun hasStopWordSupport(): Boolean = when (this) {
        ar, az, da, de, el, en, es, fi, fr, hu, id, it,
        kk, ne, nl, no, pt, ro, ru, sl, sv, tg, tr -> true
        else -> false
    }

    fun hasWordFrequencySupport(): Boolean = when (this) {
        ar, cs, de, en, es, fi, fr, it, ja, nl, pl, uk,
        pt, ru, zh, bg, bn, ca, da, el, fa, he, hi,
        hu, id, ko, lv, mk, ms, nb, ro, sh, sv, tr -> true
        else -> true
    }

    fun largestWordFrequency(): String? = when (this) {
        ar, cs, de, en, es, fi, fr, it, ja, nl, pl, pt, ru, zh -> "large"
        bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk, ms, nb, ro, sh, sv, tr, uk -> "small"
        else -> null
    }
}