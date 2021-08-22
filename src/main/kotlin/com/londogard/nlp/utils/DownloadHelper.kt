package com.londogard.nlp.utils

import com.londogard.nlp.embeddings.EmbeddingLoader.BpeDefaultEmbeddingDimension
import com.londogard.nlp.wordfreq.WordFrequencySize
import mu.KotlinLogging
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

private val logger = KotlinLogging.logger {}

@PublishedApi
internal object DownloadHelper {
    fun getStopWords(language: LanguageSupport): Path {
        val fileInfo = UrlProvider.stopwords(language)
        downloadFileIfMissing(fileInfo)

        return fileInfo.path
    }

    fun getWordFrequencies(language: LanguageSupport, size: WordFrequencySize = WordFrequencySize.Smallest): Path {
        val fileInfo = UrlProvider.wordfreq(language, size)
        downloadFileIfMissing(fileInfo)

        return fileInfo.path
    }

    private fun downloadFileIfMissing(fileInfo: FileInfo) {
        if (!Files.exists(fileInfo.path)) {
            logger.info { "Downloading ${fileInfo.description} for ${fileInfo.language} as files don't exist locally." }

            fileInfo.toUrl().saveTo(fileInfo.path)


            logger.info { "Download completed! ${fileInfo.language} ${fileInfo.description} located at ${fileInfo.path.toAbsolutePath()}" }
        }
    }

    fun getWordEmbeddings(language: LanguageSupport): Path {
        val fileInfo = UrlProvider.fastText(language)
        if (!Files.exists(fileInfo.path)) {
            val tmpPath = Files.createTempFile("tmp", ".gz")
            downloadFileIfMissing(fileInfo.copy(path = tmpPath))
            Files.newOutputStream(fileInfo.path).use { out ->
                CompressionUtil.gunzip(tmpPath).use { input -> input.copyTo(out) }
            }
            Files.deleteIfExists(tmpPath)
        }
        return fileInfo.path
    }

    // TODO improve by `data class`
    fun getSentencePieceVocabModel(language: LanguageSupport, vocabSize: Int = 10_000): Pair<Path, Path> {
        val (vocab, model) = UrlProvider.sentencePiece(language, vocabSize)
        downloadFileIfMissing(vocab)
        downloadFileIfMissing(model)

        return vocab.path to model.path
    }

    fun getBpeEmbeddings(
        language: LanguageSupport,
        vocabSize: Int = 10_000,
        dimensions: Int = BpeDefaultEmbeddingDimension
    ): Path {
        val fileInfo = UrlProvider.bpeEmbedding(language, vocabSize, dimensions)

        if (!Files.exists(fileInfo.path)) {
            val tmpPath = fileInfo.path.parent.resolve("${fileInfo.filename}.tar.gz")

            downloadFileIfMissing(fileInfo.copy(path = tmpPath))
            val uncompressPath = CompressionUtil
                .uncompressTarGz(tmpPath)
                .toFile()
                .walkBottomUp()
                .first { it.name == fileInfo.filename }
                .toPath()

            Files.move(uncompressPath, fileInfo.path, StandardCopyOption.REPLACE_EXISTING)
            Files.deleteIfExists(tmpPath)
            fileInfo.path.parent.resolve("data").toFile().deleteRecursively()
        }

        return fileInfo.path
    }

    private fun URL.saveTo(path: Path) {
        Files.createDirectories(path.parent)

        openStream().use { input ->
            path.toFile().outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}