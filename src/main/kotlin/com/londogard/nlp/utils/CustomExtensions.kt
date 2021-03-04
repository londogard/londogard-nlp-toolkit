package com.londogard.nlp.utils

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

// Taken from Kotlin stdlib

inline fun Path.readLines(charset: Charset = Charsets.UTF_8): List<String> {
    return Files.readAllLines(this, charset)
}

inline fun <T> Path.useLines(charset: Charset = Charsets.UTF_8, block: (Sequence<String>) -> T): T {
    return Files.newBufferedReader(this, charset).use { block(it.lineSequence()) }
}