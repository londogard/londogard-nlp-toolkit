package com.londogard.nlp.utils

import com.londogard.nlp.embeddings.EmbeddingLoader.BpeDefaultEmbeddingDimension
import com.londogard.nlp.wordfreq.WordFrequencySize
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@PublishedApi
internal object DownloadHelper {
    private val rootPath: Path = Paths.get(System.getProperty("user.home")).resolve(".londogard")
    private const val dataUrl: String = "https://raw.githubusercontent.com/londogard/londogard-nlp-toolkit/main/data"
    private const val bpeUrl: String = "https://nlp.h-its.org/bpemb/"
    private val stopwordPath: Path = rootPath.resolve("stopwords")
    private val wordFrequencyPath: Path = rootPath.resolve("wordfreq")
    private val embeddingPath: Path = rootPath.resolve("embeddings")
    private val bpePath: Path = rootPath.resolve("bpe")

    fun getStopWords(language: LanguageSupport): Path {
        val path = stopwordPath.resolve(language.name)
        if (!Files.exists(path)) {
            println("Language ${language.name} does not have stopwords locally. Will download (few KBs)...")

            "$dataUrl/stopwords/${language.name}".saveTo(path)

            println("Download done! ${language.name} stopwords located at ${path.toAbsolutePath()}")
        }

        return path
    }

    fun getWordFrequencies(language: LanguageSupport, size: WordFrequencySize = WordFrequencySize.Smallest): Path {
        val filename = size.toFileName(language)
        val path = wordFrequencyPath.resolve(filename)
        if (!Files.exists(path)) {
            println("Language ${language.name} does not have (${size.name}) word frequencies locally. Will download (few KBs)...")

            "$dataUrl/wordfreq/${filename}".saveTo(path)

            println("Download done! ${language.name} (${size.name}) word frequencies located at ${path.toAbsolutePath()}")
        }

        return path
    }

    fun getWordEmbeddings(language: LanguageSupport): Path {
        val filename = "cc.${language.name}.300.vec"
        val path = embeddingPath.resolve(filename)
        if (!Files.exists(path)) {
            path.parent.createDirectories()
            println("Language ${language.name} does not have word embeddings locally. Will download (could be GBs)...")
            val url = "https://dl.fbaipublicfiles.com/fasttext/vectors-crawl/$filename.gz"
            val tmpPath = Files.createTempFile("tmp", ".gz")

            url.saveTo(tmpPath)
            Files.newOutputStream(path).use { out ->
                CompressionUtil.gunzip(tmpPath).use { input -> input.copyTo(out) }
            }
            Files.deleteIfExists(tmpPath)

            println("Download completed! ${language.name} word embeddings located at ${path.toAbsolutePath()}")
        }
        return path
    }

    private fun getBpeBaseUrl(language: LanguageSupport, numMerges: Int): String =
        "$bpeUrl/$language/$language.wiki.bpe.vs$numMerges"

    // TODO improve by `data class`
    fun getBpeModelVocab(language: LanguageSupport, vocabSize: Int = 10_000): Pair<Path, Path> {
        val baseUrl = getBpeBaseUrl(language, vocabSize)
        val vocab = getBpeFile("$baseUrl.vocab")
        val model = getBpeFile("$baseUrl.model")

        return vocab to model
    }

    private fun getBpeFile(url: String): Path {
        val filename = url.takeLastWhile { it != '/' }
        val path = bpePath.resolve(filename)
        if (!Files.exists(path)) {
            println("Downloading BPE Model/Vocab/Embedding ($filename)")
            url.saveTo(path)
            println("Download completed! $filename located at ${path.parent}")
        }

        return path
    }

    fun getBpeEmbeddings(language: LanguageSupport, vocabSize: Int = 10_000, dimensions: Int = BpeDefaultEmbeddingDimension): Path {
        val filePath = bpePath.resolve("$language.wiki.bpe.vs$vocabSize.d$dimensions.w2v.txt")

        return if (Files.exists(filePath)) {
            filePath
        } else {
            val baseUrl = getBpeBaseUrl(language, vocabSize)
            val embeddingsCompressed = getBpeFile("$baseUrl.d$dimensions.w2v.txt.tar.gz")
            val tmpPath = CompressionUtil.uncompressTarGz(embeddingsCompressed)
            embeddingsCompressed.toFile().deleteRecursively()
            Files.move(tmpPath, filePath)
            bpePath.resolve("data").toFile().deleteRecursively()

            filePath
        }
    }

    private fun String.saveTo(path: Path) {
        Files.createDirectories(path.parent)

        URL(this).openStream().use { input ->
            path.toFile().outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}