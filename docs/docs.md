# Module nlp

Welcome to londogard-**nlp**-toolkits, `com.londogard:nlp`!  
This project is created to make NLP tools more accessible to the JVM world.

It includes a multitude of features, such as
1. Embeddings (Word & Sentence)
2. Tokenizers (Word, Char & Subword)
3. Stopwords, Word Frequencies & Stemming
4. Vectorizers & Encoders (TF-IDF, BM-25, OneHot, ...)
5. Classifiers (NaïveBayes, Logistic Regression w/ SGD & Transformers including HuggingFace)
6. Token Classifiers (Hidden Markov Chains & Transformers including HuggingFace)
7. Keyword Extraction

# Package com.londogard.nlp.wordfreq

The Word Frequencies are taken from `wordfreq.py` a library by [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/) and are hosted directly on the GitHub. The object looks as follows:

```kotlin
object WordFrequencies {
   fun getAllWordFrequenciesOrNull(language: LanguageSupport, size: WordFrequencySize = WordFrequencySize.Largest): Map<String, Float>?
   
   fun wordFrequency(word: String, language: LanguageSupport, minimum: Float = 0f, size: WordFrequencySize): Float // Throws if language does not support wordfreq
   fun wordFrequencyOrNull( word: String, language: LanguageSupport, minimum: Float = 0f, size: WordFrequencySize): Float?
}
```

This object has an internal cache which saves the previously loaded language. Use `WordFrequencies.getAllWordFrequenciesOrNull` to simply retrieve the WordFrequencies and use them yourself as a `Map<String, Float>`.  
Methods to recieve `zipfFrequencies` also exists.

See samples in [wordfreq.ipynb](https://github.com/londogard/londogard-nlp-toolkit/docs/samples/wordfreq.ipynb)

# Package com.londogard.nlp.embeddings

> Embeddings is a term used for the representation of words for text analysis, typically in the form of a real-valued vector that encodes the meaning of the word such that the words that are closer in the vector space are expected to be similar in meaning, more on [wikipedia.org](https://en.wikipedia.org/wiki/Word_embedding).

In `com.londogard:nlp` there's multiple embeddings supported:
- Simple word embeddings (one word = one vector)
  - [LightWordEmbeddings] which retrieves vectors from disc ad-hoc and caches the results in memory
  - [WordEmbeddings]
- Subword Embeddings 
  - [BpeEmbeddings] (using SentencePiece tokenization)
- Sentence Embeddings
  - [AverageSentenceEmbedding]
  - [USifSentenceEmbedding]
