package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.useLines
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import java.nio.file.Path
import kotlin.math.min

object EmbeddingLoader {
    const val FastTextDefaultEmbeddingDimension = 300
    const val BpeDefaultEmbeddingDimension = 50

    inline fun <reified T: Embeddings> fromLanguageOrNull(language: LanguageSupport): T? {
        return when {
            T::class == WordEmbeddings::class && language.hasWordEmbeddings() ->
                WordEmbeddings(DownloadHelper.getWordEmbeddings(language)) as T
            T::class == LightWordEmbeddings::class && language.hasWordEmbeddings() ->
                LightWordEmbeddings(DownloadHelper.getWordEmbeddings(language)) as T
            T::class == BpeEmbeddings::class && language.hasSentencePiece() ->
                BpeEmbeddings(DownloadHelper.getBpeEmbeddings(language)) as T
            else -> null
        }
    }

    inline fun <reified T: Embeddings> fromFile(path: Path): T {
        return when {
            T::class == LightWordEmbeddings::class -> LightWordEmbeddings(path) as T
            T::class == BpeEmbeddings::class -> BpeEmbeddings(path) as T
            else -> WordEmbeddings(path) as T
        }
    }

    internal fun fromFile(path: Path,
                          delimiter: Char,
                          inFilter: Set<String> = emptySet(),
                          maxWordCount: Int = Int.MAX_VALUE): Map<String, D1Array<Float>> =
        path
            .useLines { lines ->
                val iterator = lines.iterator()
                val (numLines, dimensions) = iterator.next().split(delimiter).take(2).map(String::toInt)
                val numLinesToUse = min(maxWordCount, numLines)

                iterator
                    .asSequence()
                    .map { line -> line.split(delimiter) }
                    .filter { split -> inFilter.isEmpty() || inFilter.contains(split.first()) }
                    .take(numLinesToUse)
                    .map { points ->
                        val floatArray = FloatArray(dimensions) { i -> points[i + 1].toFloat() }
                        points.first() to mk.ndarray(floatArray)
                    }
                    .toMap(LinkedHashMap(numLinesToUse)) // optimization by creating the full map directly
            }
}