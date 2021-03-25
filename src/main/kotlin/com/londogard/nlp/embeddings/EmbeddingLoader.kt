package com.londogard.nlp.embeddings

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.useLines
import org.ejml.simple.SimpleMatrix
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

    // TODO inline fun <reified T: Embeddings> fromUrl(url: String): Map<String, SimpleMatrix> = TODO("")

    internal fun fromFile(path: Path,
                          delimiter: Char,
                          inFilter: Set<String> = emptySet(),
                          maxWordCount: Int = Int.MAX_VALUE): Map<String, SimpleMatrix> =
        path
            .useLines { lines ->
                val (numLines, dimensions) = lines.first().split(delimiter).take(2).map(String::toInt)
                lines
                    .map { line -> line.split(delimiter) }
                    .filter { split -> inFilter.isEmpty() || inFilter.contains(split.first()) }
                    .take(min(maxWordCount, numLines))
                    .toList()   // Optimizing toMap()
                    .map { points ->
                        val floatArray = FloatArray(dimensions) { i -> points[i + 1].toFloat() }
                        points.first() to SimpleMatrix(1, dimensions, true, floatArray)
                    }
                    .toMap()
            }
}