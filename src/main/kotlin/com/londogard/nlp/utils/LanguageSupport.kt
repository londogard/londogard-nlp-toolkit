package com.londogard.nlp.utils

/**
 * All languages and their support.
 * For conversion from ISO-code, see https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
 * 'nb' = Norwegian Bokmål. But you can call 'no' too.
 */
enum class LanguageSupport {
    ab, ace, ady, af, ak, als, am, an, ang, ar, arc, arz, `as`, ast, atj, av, ay, az, azb,
    ba, bar, bcl, be, bg, bi, bh, bjn, bm, bn, bo, bpy, br, bs, bug, bxr,
    ca, cdo, ce, ceb, ch, chr, chy, ckb, co, cr, crh, cs, csb, cu, cv, cy,
    da, de, din, diq, dsb, dty, dv, dz, ee, eml, el, en, eo, es, et, eu, ext,
    fa, ff, fi, fj, fo, fr, frp, frr, fur, fy, ga, gag, gan, gd, gl, glk, gn, gom, got, gu, gv,
    ha, hak, haw, he, hi, hif, hr, hsb, ht, hu, hy, ia, id, ie, ig, ik, ilo, io, `is`, it, iu, ja, jam, jbo, jv,
    ka, kaa, kab, kbd, kbp, kg, ki, kk, kl, km, kn, ko, koi, krc, ks, ksh, ku, kv, kw, ky,
    la, lad, lb, lbe, lez, lg, li, lij, lmo, ln, lo, lrc, lt, ltg, lv, mai, mdf, me, mg, mh, mhr, mi, min, mk, ml, mn, mr, mrj, ms, mt, mwl, my, myv, mzn,
    na, nb, nap, nah, nds, ne, new, ng, nl, nn, no, nov, nrm, nso, nv, ny, oc, olo, om, or, os,
    pa, pag, pam, pap, pcd, pdc, pfl, pi, pih, pl, pms, pnb, pnt, ps, pt, qu, rm, rmy, rn, rs, ro, ru, rue, rw,
    sa, sah, sc, scn, sco, sd, se, sg, sh, si, sk, sl, sm, sn, so, sq, sr, srn, ss, st, stq, su, sv, sw, szl,
    ta, tcy, te, tet, tg, th, ti, tk, tl, tn, to, tpi, tr, ts, tt, tum, tw, ty, tyv, udm, ug, uk, ur, uz,
    ve, vec, vep, vi, vls, vo, wa, war, wo, wuu, xal, xh, xmf, yi, yo, za, zea, zh, zu;

    // Supported through SnowballStemmer (http://snowball.tartarus.org/)
    fun hasStemmer(): Boolean = when (this) {
        sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es, tr -> true
        else -> false
    }

    // Supported through FastText vectors (https://fasttext.cc/docs/en/crawl-vectors.html)
    fun hasWordEmbeddings(): Boolean = when (this) { // TODO add fastText vector extraction
        en, ky, xmf, mwl, tt, vec, ml, pfl, ro, war, tk, mhr, sc, am, cv, `as`,
        nn, vo, az, ia, th, ka, gl, sco, co, mt, rm, bar, zh, pt, kk, fy, pms,
        mzn, ba, cy, li, et, fa, bg, sl, ast, `is`, ja, de, hif, nds, bcl, so, ceb,
        pam, es, gom, cs, ca, new, bpy, hy, nl, lmo, ne, nap, oc, ce, mk, mg, ur,
        mr, lv, hu, diq, kn, ku, ht, scn, hsb, ar, bn, bs, pnb, af, ckb, mai, su,
        uk, ru, qu, no, bh, sr, it, eml, gd, lb, vi, eu, da, sh, tl, sah, ps, nah,
        yo, mn, id, frr, sa, pl, hi, ko, io, dv, fr, ug, gv, eo, sv, km, ilo, yi,
        be, mrj, my, sk, or, sw, fi, la, pa, el, ga, als, tr, hr, lt, azb, min, arz,
        sd, uz, si, zea, jv, te, os, he, bo, gu, wa, vls, tg, br, myv, an, ms, nso,
        ta, sq -> true
        else -> false
    }

    // Supported through NLTKs stopword lists (https://www.nltk.org/)
    fun hasStopWordSupport(): Boolean = when (this) {
        ar, az, da, de, el, en, es, fi, fr, hu, id, it,
        kk, ne, nl, no, pt, ro, ru, sl, sv, tg, tr -> true
        else -> false
    }

    // Supported through wordfreq.py datasets (https://pypi.org/project/wordfreq/)
    fun hasWordFrequencySupport(): Boolean = when (this) {
        ar, cs, de, en, es, fi, fr, it, ja, nl, pl, uk,
        pt, ru, zh, bg, bn, ca, da, el, fa, he, hi, ba, hr, rs, me,
        hu, id, ko, lv, mk, ms, nb, no, ro, sh, sv, tr -> true
        else -> false
    }

    fun largestWordFrequency(): String? = when (this) {
        ar, cs, de, en, es, fi, fr, it, ja, nl, pl, pt, ru, zh -> "large"
        bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk, ms, nb, no, ro, sh, sv, tr, uk,ba, hr, rs, me -> "small"
        else -> null
    }

    // Download custom model/vocab from here: https://nlp.h-its.org/bpemb/ or create your own.
    fun hasSentencePiece(): Boolean = when (this) {
        nb, nah, bh, eml -> false
        else -> true
    }
}