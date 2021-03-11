package com.londogard.nlp.embeddings

<<<<<<< Updated upstream
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.utils.useLines
import org.ejml.simple.SimpleMatrix
import java.nio.file.Path

object EmbeddingLoader {
    const val DefaultEmbeddingDimension = 50
    internal fun fromUrl(url: String): Map<String, SimpleMatrix> = TODO("")
    internal fun fromFile(path: Path,
                          delimiter: Char,
                          dimensions: Int,
                          inFilter: Set<String> = emptySet(),
                          maxWordCount: Int = Int.MAX_VALUE): Map<String, SimpleMatrix> =
            path
                .useLines { lines ->
                    lines
                        .filter { line -> inFilter.isEmpty() || inFilter.contains(line.takeWhile { it != delimiter }) }
                        .mapNotNull { line ->
                            line
                                .split(delimiter)
                                .filterNot { it.isEmpty() || it.isBlank() }
                                .takeIf { it.size > dimensions }
                                ?.let { elems ->
                                    val key = elems.first()
                                    val value = FloatArray(dimensions) { i -> elems[i + 1].toFloat() }
                                    key to value
                                }
                        }
                        .take(maxWordCount)
                        .map { (key, value) -> key to SimpleMatrix(1, dimensions, true, value) }
                        .toMap()
                }

    internal fun byLanguage(languageSupport: LanguageSupport): Map<String, SimpleMatrix> = TODO("") // https://fasttext.cc/docs/en/crawl-vectors.html
=======
object EmbeddingLoader {
>>>>>>> Stashed changes
}