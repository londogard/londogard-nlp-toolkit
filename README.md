# londogard-nlp-toolkit
Londogard Natural Language Processing Toolkit written in Kotlin for the JVM.

:warning: Still Work In Progress :warning:  

A lot of the modules are still in a good place 🥳
✔️WordEmbeddings (`WordEmbeddings` & `LightWordEmbeddings`)
✔️Stopwords
✔️WordFrequencies
✔️Tokenizer (`CharTokenizer` & `SimpleTokenizer`)
✔️Stemmer
✔️Basic Trie
✔️Sentence Embeddings (`AvgSentenceEmbeddings` & `USifEmbeddings`)

But there's a lot of usable stuffs such as Stopwords, WordFrequencies, Stemmer, Tokenizer & WordEmbeddings!

## Language Support
`LanguageSupport` is a core Enum which involves everything that's supported.  
Most of the supported things such as `Stopwords` and `WordFrequencies` are 
really simple in the core, e.g. a `Set<String>` or `Map<String, Float>`, but 
are usually something which you want to get directly to be able to move forward 
at pace and later specializing it for your NLP tool.  

In this library these are given for plenty of languages and all the tools are built, 
or using data, from other established sources such as `SnowballStemmer`, `wordfreq.py` and `NLTK` (stopwords).

TODO - add table of support!


## Stopwords
Stopwords are words that don't really contribute to the core of a sentence, such as "is" and "and".  
It's very common, especially if using traditional Machine Learning, to filter and remove stopwords before creating your features.

In this library stopwords are supplied through the `Stopwords` object.
```kotlin
object Stopwords {
    fun isStopword(word: String, language: LanguageSupport): Boolean
    fun stopwords(language: LanguageSupport): Set<String> // Throws if language does not support stopwords
    fun stopwordsOrNull(language: LanguageSupport): Set<String>?
}
```

### Simple Usage
```kotlin
isStopword("and", LanguageSupport.en) // true
isStopword("doctor", LanguageSupport.en) // false

stopwords(LanguageSupport.en) // Set<String>
stopwords(LanguageSupport.nb) // Throws !!
stopwordsOrNull(LanguageSupport.nb) // null
```

## WordFrequencies
WordFrequencies supplies word freqienecies for a lot of languages (see LanguageSupport for exactly which). 
The frequencies are based on the amazing `wordfreq.py` by [LuminosoInsight](https://github.com/LuminosoInsight/wordfreq/).

Word Frequency is something commonly used for a lot of NLP applications and it's very helpful to have a prebuilt list if you're doing demos or something. Otherwise it's often wise to create your own word frequency table.

In this library all WordFrequencies are supplied from an object with built-in cache.

```kotlin
object WordFrequencies {
   fun getAllWordFrequenciesOrNull(language: LanguageSupport, size: WordFrequencySize = WordFrequencySize.Largest): Map<String, Float>?
   
   fun wordFrequency(word: String, language: LanguageSupport, minimum: Float = 0f, size: WordFrequencySize): Float // Throws if language does not support wordfreq
   fun wordFrequencyOrNull( word: String, language: LanguageSupport, minimum: Float = 0f, size: WordFrequencySize): Float?
}
```

Where all `wordFrequency` can be exchanged for `zipfFrequency` if you prefer.

### Simple Usage
```kotlin
WordFrequencies.getAllWordFrequenciesOrNull(sv, Largest) // Map<String, Float>
WordFrequencies.wordFrequency("hej", sv) // 2.9512093E-4
ordFrequencies.wordFrequency("dadadad", sv) // 0.0
ordFrequencies.wordFrequencyOrNull("dadadad", sv) // null
```

## Stemmer
To explain stemming I'll excerpt [wikipedia.org](https://en.wikipedia.org/wiki/Stemming)
> In linguistic morphology and information retrieval, stemming is the process of reducing inflected (or sometimes derived) words to their word stem, base or root form—generally a written word form.

In other words, a stemming algorithm could reduce the words fishing, fished, and fisher to the stem fish.  
In this library all stemmers are built by the class `Stemmer` and if the stemmer has no support for the language you wish it'll default to a `PorterStemmer`.

```kotlin
class Stemmer(language: LanguageSupport) {
    fun stem(word: String): String
}
```

One can also call the stemmer using the companion object, it'll then cache the last stemmer used and if you request a new one it'll load that into cache removing the former.

```kotlin
fun Stemmer.stem(word: String, language: LanguageSupport): String
```

### Simple use-case

```kotlin
// 1. Using a class
val stemmer = Stemmer(en)
stemmer.stem("fishing") // "fish"

// 2. Using a companion object
Stemmer.stem("fishing", en) // "fish"
```

## Tokenizer
Tokenization is a way to build tokens out of sentences, or even large texts, where a token can be a word, a subword or even a single character. Text is tokenized to create pieces which can be used as features.  
In this library tokenizers are built on a simple interface.
```kotlin
interface Tokenizer {
    fun split(text: String): List<String>
}
```
Currently there exists two tokenizers:
1. `CharTokenizer` which simply returns an array of all characters.
2. `SimpleTokenizer(splitContraction: Boolean = false, whitespaceRegex: String = WHITESPACE)` which returns all words split by whitespace
    - `WHITESPACE = "[^\\S\\r\\n]+"`
    - Includes some special logic which splits on delimeters also (`...`, punctuations etc) based on some simple requirements to not split for example `2,500`.

There's also a third tokenizer being developed currently - `SubwordTokenizer` which creates a balance between BPE & SentencePiece algorithms.

### Simple use-case

```kotlin
// 1. CharTokenizer
val tokenizer = CharTokenizer()
tokenizer.split("hello world!")
// Out: [h, e, l, l, o,  , w, o, r, l, d, !]


// 2. SimpleTokenizer
val tokenizer = SimpleTokenizer()
tokenizer.split("Hello world! This is a fantastic day, isn't it?")
// Out: [Hello, world, !, This, is, a, fantastic, day, ,, isn't, it, ?]
```

## Word Embeddings

