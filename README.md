[![Maven Central](https://img.shields.io/maven-central/v/com.londogard/nlp.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.londogard%22%20AND%20a:%22nlp%22)
[![](https://jitpack.io/v/com.londogard/londogard-nlp-toolkit.svg)](https://jitpack.io/#com.londogard/londogard-nlp-toolkit)

# londogard-nlp-toolkit
Londogard Natural Language Processing Toolkit written in Kotlin for the JVM.  
This toolkit will be used throughout Londogard libraries/products such as our Summarizer, Text-Generation & more.

First of, `LanguageSupport.<ISO_COUNTRY_CODE>`helps figuring out what is and isn't supported for certain languages. Utilities that makes sense to provide your own data, like Word Embeddings & Tokenizers, are available for any language if you provide your own models.

| Feature                     | Implementations (if multiple)                                             | Languages                                                                                                                                 | Based On                                                                                                                                                | Notes                                                                                                                                                                                                                                                                                      |
|-----------------------------|---------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Word Embeddings**         | - `WordEmbeddings`<br>- `LightWordEmbeddings`<br>- `BPEEmbeddings`        | - ∞ languages if self-provided<br>- 275 through `BPEEmb`-embeddings<br>- 157 through `fastText`-embeddings                                | - `fastText` embeddings downloaded from [fastText.cc](https://fasttext.cc)<br>- `bpe-emb` embeddings downloaded from [bpeemb](https://bpemb.h-its.org/) | - `LightWordEmbeddings`is great for memory (e.g. running on Raspberry Pi) as it only loads the most common embeddings and cache ad-hoc<br>- `BPEEmbeddings` are also great for memory but requires subword tokenization (built-in so you don't have to think about it unless you optimize) |
| **Sentence Embeddings**     | - `AverageSentenceEmbeddings`<br>- `USifSentenceEmbeddings`               |                                                                                                                                           | USif is based on [Unsupervised Random Walk Sentence Embeddings: A Strong but Simple Baseline](https://aclanthology.org/W18-3012/)                       |                                                                                                                                                                                                                                                                                            |
| **Tokenizers**              | - Word<br>- Char<br>- Subword (SentencePiece)                             | - Subword support 275 languages through `bpemb`                                                                                           | - SentencePiece is using the google implementation via [DJL](https://github.com/deepjavalibrary/djl)                                                    |                                                                                                                                                                                                                                                                                            |
| **Stopwords**               |                                                                           | `ar, az, da, de, el, en, es, fi, fr, hu, id, it, kk, ne, nl, no, pt, ro, ru, sl, sv, tg & tr`                                             | NLTK stopword lists                                                                                                                                     |                                                                                                                                                                                                                                                                                            |
| **Word Frequencies**        |                                                                           | `ar, cs, de, en, es, fi, fr, it, ja, nl, pl, uk, pt, ru, zh, bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk, ms, nb, ro, sh, sv & tr` | `wordfreq.py` by [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/)                                                                        | Some of these has "Large Word Frequency" which is much larger, find it through `LanguageSupport.<LANG_CODE>.largestWordFrequency()`                                                                                                                                                        |
| **Stemming**                |                                                                           | `sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es & tr`                                                                                 | [Snowball Stemmer](https://snowballstem.org/)                                                                                                           |                                                                                                                                                                                                                                                                                            |
| **Vectorization**           | - BagOfWords (`CountVectorizer` & `HashVectorizer`)<br>- TF-IDF<br>- BM25 |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| **Classifiers**             | - LogisticRegression (using Gradient Descent)<br>- NaïveBayes             |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| **Regression**              | - LinearRegression                                                        |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| **Sequence Classification** | - HiddenMarkovModel                                                       |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| 🚧 Keyword Extraction        |                                                                           |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| 🚧 Neural Network            |                                                                           |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |
| 🚧 spaCy-like API            |                                                                           |                                                                                                                                           |                                                                                                                                                         |                                                                                                                                                                                                                                                                                            |

## Installation
**MavenCentral**
```kotlin
implementation("com.londogard:nlp:1.0.0")
```

**Jitpack**

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
API like scikit-learn, spark-ml & spaCy. WIP