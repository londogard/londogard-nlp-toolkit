[![Maven Central](https://img.shields.io/maven-central/v/com.londogard/nlp.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.londogard%22%20AND%20a:%22nlp%22)
<a href="https://www.buymeacoffee.com/hlondogard" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-green.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>

# londogard-nlp-toolkit
Londogard Natural Language Processing Toolkit written in Kotlin for the JVM.  
This toolkit will be used throughout Londogard libraries/products such as our Summarizer, Text-Generation & more.

The `LanguageSupport` enum is used to determine what support different tools like Embeddings or Stopwords have out-of-the-box.

| Tool                                       | Info                                                                                                                                                                                                                            | Docs | Samples |
|--------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------|---------|
| Word Embeddings                            | Word & Subword Embeddings available in 157 ([fastText.cc](https://fasttext.cc)) & 275 languages ([bpemb](https://bpemb.h-its.org/)) out-of-the-box.                                                                             ||
| Sentence Embeddings                        | Average & [Unsupervised Random Walk Sentence Embeddings](https://aclanthology.org/W18-3012/)                                                                                                                                    ||
| Stopwords                                  | Supports 23 languages out-of-the-box through NLTK's list of stopword                                                                                                                                                            ||
| Word Frequencies                           | Supports 34 languages out-of-the-box through [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/) word frequency tables                                                                                              ||
| Stemming                                   | Supports 14 languages out-of-the-box using [Snowball Stemmer](https://snowballstem.org/) under the hood                                                                                                                         |
| Tokenizers                                 | Char, Word, Subword & Sentence Tokenizer support! SentencePiece? HuggingFace? It's there!                                                                                                                                       ||
| Vectorizers & Encoders                     | BagOfWords, TF-IDF, BM25 & OneHot                                                                                                                                                                                               ||
| Keyword Extractions                        | CooccurenceKeywords based on algorithm proposed in [DOI:10.1142/S0218213004001466](https://www.researchgate.net/publication/2572200_Keyword_Extraction_from_a_Single_Document_using_Word_Co-occurrence_Statistical_Information) ||
| Machine Learning                           | LogisticRegression Classifier (using Gradient Descent), NaïveBayes (binary) & Hidden Markov Model (HMM) as Sequence Classifier                                                                                                  ||
| Deep Learning (Transformers / HuggingFace) | `ClassifierPipeline` and `TokenClassifierPipeline` which supports HuggingFace ONNX model-names & PyTorch from local files                                                                                                       ||
| spaCy-like API                             | 🚧WIP🚧                                                                                                                                                                                                                         |||

## Installation

**MavenCentral**  
```kotlin
implementation("com.londogard:nlp:$version")
```

## Guides

1. Add ML Classification
2. Add Transformer Classification
3. Add NER via HMM
4. Add NER via Transformer
5. Add Preprocessing
6. ...


Please see the Kotlin Notebook [README.ipynb](https://github.com/londogard/londogard-nlp-toolkit/blob/main/README.ipynb) (rendered in browser via GitHub if you don't wanna play with it locally).
