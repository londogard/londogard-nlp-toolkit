package com.londogard.nlp.utils

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal object DownloadHelper {
    private val rootPath: Path = Paths.get(System.getProperty("user.home")).resolve(".londogard")
    private val stopwordPath: Path = rootPath.resolve("stopwords")
    private val wordFrequencyPath: Path = rootPath.resolve("wordfreq")

    fun String.saveTo(path: String) {
        URL(this).openStream().use { input ->
            FileOutputStream(File(path)).use { output ->
                input.copyTo(output)
            }
        }
    }

//    fun languageExist(language: Languages): Boolean = language.getPath(rootPath).toFile().exists()

    /**
     * 1. Download to temp directory
     * 2. Extract embeddings into 'summarize-embeddings' which is placed in root of users home folder.
     */
    fun downloadGloveEmbeddings() {
//        if (embeddingsExist()) {
//            println("Embeddings exist in path $rootPath, early exiting...")
//            return
//        }

        val tempFile = Files.createTempFile("glove", ".zip")
        val tempPath = tempFile.toAbsolutePath().toString()
        val customDir = rootPath.toFile()

        if (!customDir.exists()) customDir.mkdir()

        println("Downloading X GB of Glove Word Embeddings (this will take a while, ~1 GB)...")
        "http://downloads.cs.stanford.edu/nlp/data/glove.6B.zip".saveTo(tempPath)
        println("Download done!")
        println("Extracting 50d word embeddings (from $tempPath to $customDir). Extract your own if you want larger.")
    }

    @JvmStatic
    fun main(args: Array<String>) {

    }
}