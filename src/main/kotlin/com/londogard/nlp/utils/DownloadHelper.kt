package com.londogard.nlp.utils

import com.londogard.nlp.wordfreq.WordFrequencySize
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal object DownloadHelper {
    private val rootPath: Path = Paths.get(System.getProperty("user.home")).resolve(".londogard")
    private val dataUrl: String = "https://raw.githubusercontent.com/londogard/londogard-nlp-toolkit/main/data"
    private val stopwordPath: Path = rootPath.resolve("stopwords")
    private val wordFrequencyPath: Path = rootPath.resolve("wordfreq")

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

    private fun String.saveTo(path: Path) {
        Files.createDirectories(path.parent)
        URL(this).openStream().use { input ->
            path.toFile().outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}