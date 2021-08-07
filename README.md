[![](https://jitpack.io/v/com.londogard/londogard-nlp-toolkit.svg)](https://jitpack.io/#com.londogard/londogard-nlp-toolkit)

# londogard-nlp-toolkit
Londogard Natural Language Processing Toolkit written in Kotlin for the JVM.  
This toolkit will be used throughout Londogard libraries/products such as our Summarizer, Text-Generation & more.

First of, `LanguageSupport.<ISO_COUNTRY_CODE>`helps figuring out what is and isn't supported for certain languages. Utilities that makes sense to provide your own data, like Word Embeddings & Tokenizers, are available for any language if you provide your own models.

- ✔️Word Embeddings including basic Word Embeddings, 'Light Word Embeddings' & BytePairEmbeddings
    - The Embeddings include automated downloads of languages to simplify your life, unless a path is specified.
    - WordEmbeddings include 157 languages via `fastText`-embeddings ([fastText.cc](https://fasttext.cc/docs/en/crawl-vectors.html))
    - BPE-Embeddings include 275 languages via `BPEmb`-embeddings ([nlp.h-its.org](https://nlp.h-its.org/bpemb/))
    - All embeddings support ∞ languages through your own self-trained embeddings if a file-path is supplied!
- ✔️Sentence Embeddings including `AvgSentenceEmbeddings` & `USifEmbeddings`
- ✔️Tokenizers including Word, Char & Subword (SentencePiece) tokenizers (simple to add custom logic)
    - SentencePiece include 275 languages via `BPEmb`-embeddings ([nlp.h-its.org](https://nlp.h-its.org/bpemb/)) with 8 vocab-sizes (1000, 3000, 10_000, 25_000, 50_000, 100_000, 200_000).
        - Of course possible to supply your own tokenizer if you've a path to a trained one!
- ✔️Stopwords based on NLTKs list
    - Supporting: ar, az, da, de, el, en, es, fi, fr, hu, id, it, kk, ne, nl, no, pt, ro, ru, sl, sv, tg & tr
- ✔️Word Frequencies based on `wordfreq.py` by [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/).
    - Supporting: ar, cs, de, en, es, fi, fr, it, ja, nl, pl, uk, pt, ru, zh, bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk, ms, nb, ro, sh, sv & tr. Some of these support "Large Word Frequency", call `LanguageSupport.<LANG_CODE>.largestWordFrequency()` to see if `large` variant is available.
- ✔️Stemmer based on [Snowball Stemmer](https://snowballstem.org/)
    - Supporting: sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es & tr
- ✔️Trie - Just a basic utility that is to be used for a custom SubwordTokenizer in the future.
- 🚧✔️Vectorizer/Transformer
    - ✔️CountVectorizer (Bag of Words), TF-IDF & BM-25
    - Documentation will be added soon.
- 🚧Keyword Extraction
- [Future] Classifiers
- [Future] Neural Networks (99% it'll only be for inference via PyTorch / ONNX)
- [Future] ???


## Installation
**Jitpack (the easiest)**

Add the following to your build.gradle. `$version` should be equal to the version supplied by tag above.
```bash
repositories {
  maven { url "https://jitpack.io" }
}
dependencies {
  implementation 'com.londogard:londogard-nlp-toolkit:$version'
}
```

**GitHub Packages** is also available, look into their docs on how to use this or see other libraries by _londogard_.

## Usage

Please see the Kotlin Notebook [README.ipynb](https://github.com/londogard/londogard-nlp-toolkit/blob/main/README.ipynb) (rendered in browser via GitHub if you don't wanna play with it locally).

----

# Machine Learning with Londogard NLP Toolkit
API like scikit-learn / spark-ml.

**Preprocessors**
See the rest of Londogard NLP Toolkit (tokenizer etc)

**Vectorization**
Vectorizer = Fit, FitTransform, Transform
Transformer = Fit, FitTransform, Transform

**Models**
Regression = Fit, FitTransform, Transform
Classifier = Fit, FitTransform, Transform

**Distances**
Cosine / Euclidean / ...

All implement `BaseTool`

----

TODO
- https://github.com/Kotlin/dataframe vs https://github.com/holgerbrandl/krangl
- EJML vs DJL vs ND4j vs Native