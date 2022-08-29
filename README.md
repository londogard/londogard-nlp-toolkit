[![Maven Central](https://img.shields.io/maven-central/v/com.londogard/nlp.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.londogard%22%20AND%20a:%22nlp%22)
<a href="https://www.buymeacoffee.com/hlondogard" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-green.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>

# londogard-nlp-toolkit
Londogard Natural Language Processing Toolkit written in Kotlin for the JVM.  
This toolkit will be used throughout Londogard libraries/products such as our Summarizer, Text-Generation & more.

First of, `LanguageSupport.<ISO_COUNTRY_CODE>`helps figuring out what is and isn't supported for certain languages. Utilities that makes sense to provide your own data, like Word Embeddings & Tokenizers, are available for any language if you provide your own models.

<details>
<summary>🚀 <b>Word Embeddings</b> </summary>

NLP currently supports three types of embeddings, namely:

1. `WordEmbeddings`
    - Built-in support for 157 languages via [`fastText.cc`](https://fasttext.cc/)
2. `LightWordEmbeddings`
    - Highly efficient cache reducing memory requirements from GB to MB
    -  Built-in support for 157 languages via [`fastText.cc`](https://fasttext.cc/)
3. `BPEEmbeddings`
    - Subword embeddings (sentencepiece) that achieve +-5% accuracy using few MB rather than GB of data
    - Built in support for 275 languages via [bpemb](https://bpemb.h-its.org/) 

</details>

<details>
<summary>🚀 <b>Sentence Embeddings</b> </summary>

NLP currently supports two types of sentence embeddings, namely:
1. `AverageSentenceEmbeddings`
2. `USifSentenceEmbeddings`
    - Implementation of [Unsupervised Random Walk Sentence Embeddings: A Strong but Simple Baseline](https://aclanthology.org/W18-3012/)

</details>

<details>
<summary>🚀 <b>Tokenizers</b> </summary>

NLP supports 4 types of tokenizations, namely:

1. Word
2. Char
3. Subword (SentencePiece)
    - Subword support 275 languages through `bpemb` 
    - SentencePiece is using the google implementation via [DJL](https://github.com/deepjavalibrary/djl)
4. [HuggingFaceTokenizer](https://huggingface.co/docs/tokenizers/python/latest/) through the Rust API. Provided by DJL.
5. Sentence

</details>

<details>
<summary>🚀 <b>Stopwords</b> </summary>

NLP support Stopwords for the following languages:  
`ar, az, da, de, el, en, es, fi, fr, hu, id, it, kk, ne, nl, no, pt, ro, ru, sl, sv, tg & tr`

It's using the great NLTK Stopword list

</details>

<details>
<summary>🚀 <b>Word Frequencies</b> </summary>

NLP support Word Frequencies based on open data for the following languages:  
`ar, cs, de, en, es, fi, fr, it, ja, nl, pl, uk, pt, ru, zh, bg, bn, ca, da, el, fa, he, hi, hu, id, ko, lv, mk, ms, nb, ro, sh, sv & tr`

It's using the great `wordfreq.py` data created by [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/)

</details>

<details>
<summary>🚀 <b>Stemming</b> </summary>

NLP supports Stemming for the following languages:  
`sv, nl, en, fi, fr, de, hu, it, no, pt, ro, ru, es & tr` 

It's using the great [Snowball Stemmer](https://snowballstem.org/) under the hood.

</details>

<details>
<summary>🚀 <b>Vectorization</b> </summary>

NLP currently support three types of vectorization of text, namely:

 1. BagOfWords (`CountVectorizer` & `HashVectorizer`)
2. TF-IDF
3. BM25 (an improvement on top of TF-IDF)

</details>

<details>
<summary>🚀 <b>Classifiers</b> </summary>

NLP supports two classifiers currently, namely:

1. LogisticRegression (using Gradient Descent)
2. NaïveBayes

</details>

<details>
<summary>🚀 <b>Sequence Classification</b> </summary>

NLP supports one sequence classifier, namely:

1. Hidden Markov Model (HMM)

</details>

<details>
<summary>🚀 <b>Keyword Extraction</b> </summary>

NLP has one implementation of unsupervised Keyword Extraction, namely:

1. CooccurenceKeywords based on algorithm proposed in [DOI:10.1142/S0218213004001466](https://www.researchgate.net/publication/2572200_Keyword_Extraction_from_a_Single_Document_using_Word_Co-occurrence_Statistical_Information)

</details>

<details>
<summary>🚀 <b>Neural Networks</b> </summary>

`ClassifierPipeline` and `TokenClassifierPipeline` exists in `1.2.0-BETA`. These pipelines can run directly from HuggingFace Hub using ONNX models and from local filepath using PyTorch models that has been JIT TorchScript-exported (a two-liner).

</details>

<details>
<summary>🚧 <b>spaCy-like API</b> </summary>

Work in progress
</details>

## Installation

**MavenCentral**  
```kotlin
implementation("com.londogard:nlp:$version")
```

**GitHub Packages** is also available, look into their docs on how to use this or see other libraries by _londogard_.

## Usage

Please see the Kotlin Notebook [README.ipynb](https://github.com/londogard/londogard-nlp-toolkit/blob/main/README.ipynb) (rendered in browser via GitHub if you don't wanna play with it locally).
