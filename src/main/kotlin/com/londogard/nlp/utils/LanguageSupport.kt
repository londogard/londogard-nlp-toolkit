package com.londogard.nlp.utils

/**
 * All languages and their support.
 * For conversion from ISO-code, see https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
 */
enum class LanguageSupport {
    af, ar, an, am, `as`, ba, bh, cs, de, en, es, fi, fr, it, ja, nl, pl, pt, ru, zh,
    bg, bn, ca, da, el, eu, fa, he, hi, hu, hy, id, ko, lv, mk,
    ms, nb, ro, sh, sv, sq, tr, uk, az, kk, ne, no, sl, tg,
    als, ast, pnb, nah, sd, mr, os, nap, te, gd, myv, tt, bo, hif, arz, ilo, ps,
    lo, mzn, lb, my, yi, pa, mwl, sc, io, bar, sw, bcl, new, yo, zea, azb, su,
    gom, min, oc, th, dv, scn, frr, bpy, et, vo, wa, hr, pam, sco, cy, uz,
    fy, ml, so, sr, sa, mg, mt, ky, ht, vi, tan, vec, km, nds, vls, eo, li,
    tl, ug, hsb, be, ka, br, lmo, ta, ce, pms, ia, gv, gl, tk, mhr, ku, kn,
    `is`, nso, nn, ur, bs, or, ckb, mrj, mai, co, eml, diq, jv, xmf, pfl, ga,
    si, qu, sah, lt, ceb, cv, war, la, rm, mn, sk, gu;

    // Supported through SnowballStemmer (http://snowball.tartarus.org/)
    fun hasStemmer(): Boolean = when(this) {
        sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es, tr -> true
        else -> false
    }

    // Supported through FastText vectors (https://fasttext.cc/docs/en/crawl-vectors.html)
    fun hasWordEmbeddings(): Boolean = when(this) { // TODO add fastText vector extraction
//        pnb, nah, sd, mr, ko, os, sq, no, da, nap, te, lv, gd, myv, tt, he,
//        bo, hif, arz, uk, an, ar, ilo, ca, ps, mk, lb, mzn, my, am, yi, pa,
//        sh, mwl, `as`, sc, io, bar, sw, bcl, ms, tr, new, yo, zea, azb, ba,
//        su, gom, min, oc, pt, th, dv, scn, frr, bpy, et, vo, wa, hr, eu, pam,
//        sco, cy, uz, sv, fy, de, ml, so, sr, sa, mg, mt, af, ky, bg, ht, vi,
//        tan, cs, vec, km, el, nds, vls, eo, hi, li, tl, ug, hsb, ne, be, ka,
//        br, lmo, ta, nl, sl, bn, ce, fi, es, pms, bh, ia, hu, gv, tg, fr, gl,
//        tk, mhr, ast, ku, id, kn, `is`, nso, nn, ur, bs, or, ckb, mrj, mai, co,
//        kk, als, ro, eml, diq, jv, ru, az, xmf, pfl, ga, ja, si, qu, pl, sah,
//        lt, en, ceb, fa, cv, it, hy, war, la, rm, mn, sk, zh, gu -> true
        else -> false
    }

    // fun hasLargeWordEmbeddingSupport(): Boolean = TODO("")
    //fun hasSmallWordEmbeddingSupport(): Boolean = TODO("")

    // Supported through NLTKs stopword lists (https://www.nltk.org/)
    fun hasStopWordSupport(): Boolean = when (this) {
        ar, az, da, de, el, en, es, fi, fr, hu, id, it,
        kk, ne, nl, no, pt, ro, ru, sl, sv, tg, tr -> true
        else -> false
    }

    // Supported through wordfreq.py datasets (https://pypi.org/project/wordfreq/)
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