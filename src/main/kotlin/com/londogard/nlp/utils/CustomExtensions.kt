package com.londogard.nlp.utils

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileAttribute

/** Custom extensions for Path taken from Kotlin EXPERIMENTAL. */

// Taken from Kotlin stdlib (EXPERIMENTAL)
internal inline fun Path.readLines(charset: Charset = Charsets.UTF_8): List<String> =
    Files.readAllLines(this, charset)

inline fun <T> Path.useLines(charset: Charset = Charsets.UTF_8, block: (Sequence<String>) -> T): T {
    return Files.newBufferedReader(this, charset).use { block(it.lineSequence()) }
}

internal inline fun Path.createDirectories(vararg attributes: FileAttribute<*>): Path =
    Files.createDirectories(this, *attributes)