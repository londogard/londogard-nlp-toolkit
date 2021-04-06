package com.londogard.nlp.utils

import com.londogard.nlp.embeddings.EmbeddingLoader
import com.londogard.nlp.wordfreq.WordFrequencySize
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

object UrlProvider {
    private const val githubDataUrl: String = "https://raw.githubusercontent.com/londogard/londogard-nlp-toolkit/main/data"
    private const val bpeUrl: String = "https://nlp.h-its.org/bpemb/"

    private val rootPath: Path = Paths.get(System.getProperty("user.home")).resolve(".londogard")
    private val stopwordPath: Path = rootPath.resolve("stopwords")
    private val wordFrequencyPath: Path = rootPath.resolve("wordfreq")
    private val embeddingPath: Path = rootPath.resolve("embeddings")
    private val bpePath: Path = rootPath.resolve("bpe")

    fun fastText(language: LanguageSupport): FileInfo {
        val filename = "cc.${language.name}.300.vec"
        val url = "https://dl.fbaipublicfiles.com/fasttext/vectors-crawl/$filename.gz"
        return FileInfo(filename, embeddingPath.resolve(filename), url, "fastText (GB(s))", language)
    }

    fun bpeEmbedding(language: LanguageSupport, vocabSize: Int, dimensions: Int): FileInfo {
        val filename = "$language.wiki.bpe.vs$vocabSize.d$dimensions.w2v.txt"
        val url = "${getBpeBaseUrl(language, vocabSize)}.d$dimensions.w2v.txt.tar.gz"

        return FileInfo(filename, bpePath.resolve(filename), url, "bpemb embeddings (100KB - 45MB)", language)
    }

    fun sentencePiece(language: LanguageSupport, vocabSize: Int): Pair<FileInfo, FileInfo> {
        val baseUrl = getBpeBaseUrl(language, vocabSize)
        val baseFileName = baseUrl.takeLastWhile { char -> char != '/' }
        val vocabFilename = "$baseFileName.vocab"
        val modelFilename = "$baseFileName.model"
        val vocab = FileInfo(vocabFilename, bpePath.resolve(vocabFilename), "$baseUrl.vocab", "sentencepiece (bpemb) vocab (<10 KB)", language)
        val model = FileInfo(modelFilename, bpePath.resolve(modelFilename), "$baseUrl.model", "sentencepiece (bpemb) model (<4 MB)", language)

        return vocab to model
    }

    fun stopwords(language: LanguageSupport): FileInfo =
        FileInfo(language.toString(), stopwordPath.resolve(language.toString()),"$githubDataUrl/stopwords/$language", "stopwords (<2 KB)", language)

    fun wordfreq(language: LanguageSupport, size: WordFrequencySize): FileInfo {
        val filename = size.toFileName(language)
        return FileInfo(filename, wordFrequencyPath.resolve(filename), "$githubDataUrl/wordfreq/$filename", "wordfreq (<3 MB)", language)
    }

    private fun getBpeBaseUrl(language: LanguageSupport, vocabSize: Int): String =
        "$bpeUrl/$language/$language.wiki.bpe.vs$vocabSize"
}