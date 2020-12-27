package com.londogard.nlp.wordfreq

import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.math.pow

//import com.londogard.nlp.utils.Languages

object WordFreq {
    private var cache: Pair<Path, Map<Float, List<String>>>? = null
//    fun wordFrequency(word: String, languages: Languages, minimum: Float = 0f): Float = TODO("implement")
//
//    fun zipfFrequency(word: String, languages: Languages, minimum: Float = 0f): Float = TODO("implement")

    internal fun unpackFile(path: Path): Map<Float, List<String>> {
        val unpackedFile by lazy {
            path.toFile()
                .inputStream()
                .let(::GZIPInputStream)
                .let(GZIPInputStream::bufferedReader)
                .useLines { lines ->
                    lines
                        .mapIndexed { index, line ->
                            10f.pow(-index.toFloat() / 100) to line.split(' ').filterNot(String::isEmpty)
                        }
                        .filter { (_, tokens) -> tokens.isNotEmpty() }
                        .toMap()
                }
        }

        return if (cache?.first == path) cache?.second ?: unpackedFile
        else {
            cache = path to unpackedFile
            unpackedFile
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

    }
}
