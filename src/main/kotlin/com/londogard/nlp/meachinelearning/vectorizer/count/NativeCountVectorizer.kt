package com.londogard.nlp.meachinelearning.vectorizer.count

import ai.djl.ndarray.types.Shape
import com.londogard.nlp.meachinelearning.native.NativeManager
import com.londogard.nlp.meachinelearning.native.NativeNDArray
import com.londogard.nlp.meachinelearning.datatypes.Coordinate
import com.londogard.nlp.meachinelearning.datatypes.Percent
import com.londogard.nlp.meachinelearning.datatypes.PercentOrCount
import com.londogard.nlp.utils.IterableExtensions.identityCount
import com.londogard.nlp.utils.IterableExtensions.ngrams
import java.nio.FloatBuffer
/**
class NativeCountVectorizer(
    val minDf: PercentOrCount = Percent(0.0),
    val maxDf: PercentOrCount = Percent(1.0),
    val ngramRange: IntRange = 1..1
): NativeVectorizer<Float> {
    private lateinit var vectorization: Map<String, Int>

    init {
        if (ngramRange.first <= 0) throw IllegalArgumentException("ngramRange must be larger than 0")
    }

    /** Does a fitTransform */
    override fun fit(input: List<List<String>>) {
        fitTransform(input)
    }

    override fun transform(input: List<List<String>>): NativeNDArray {
        val dataV = input.map { row -> row.mapNotNull(vectorization::get).identityCount() }

        // val data = FloatBuffer.wrap(FloatArray(dataV.size) { i -> dataV[i].count })
        // val cols = LongArray(dataV.size) { i -> dataV[i].col.toLong() }
        // val rows = LongArray(dataV.size) { i -> dataV[i].row.toLong() }

        return NativeManager.ndManager.createCoo(
            data, arrayOf(rows, cols),
            Shape(input.size.toLong(), vectorization.size.toLong())
        )
    }

    override fun fitTransform(input: List<List<String>>): NativeNDArray {
        val vocab = mutableMapOf<String, Int>()
        val vectorized = input
            .asSequence()
            .map { tokens ->
                ngramRange
                    .flatMap { tokens.ngrams(it) }
                    .map { vocab.getOrPut(it) { vocab.size } }
                    .identityCount()
            }
            .toList()

        val totalCount = input.sumOf { it.size }

        val toRemove by lazy {
            val dfArray = IntArray(vocab.size)
            vectorized.forEach { rowCount -> rowCount.keys.forEach { i -> dfArray[i] += 1 } }
            dfArray.forEachIndexed { index, count ->
                if (minDf.isLesserThan(count, totalCount) && maxDf.isGreatherThan(count, totalCount)) Unit
                else dfArray[index] = -1
            }
            dfArray
        }

        val (vec, remap) = vocab
            .toList()
            .filterNot { (_, index) -> toRemove[index] == -1 }
            .mapIndexed { index, (key, oldIndex) -> (key to index) to (oldIndex to index) }
            .unzip()
        vectorization = vec.toMap()

        val remapMap = remap.toMap()
        val remappingArray = IntArray(vocab.size){ i -> remapMap.getOrDefault(i, -1) }

        val countMapFiltered = vectorized
            .flatMapIndexed { row, docCount ->
                docCount
                    .filter { (key, _) -> remappingArray[key] != -1 }
                    .map { (key, count) -> Coordinate(row, remappingArray[key], count.toFloat()) }
            }

        val data = FloatBuffer.wrap(FloatArray(countMapFiltered.size) { i -> countMapFiltered[i].count })
        val cols = LongArray(countMapFiltered.size) { i -> countMapFiltered[i].col.toLong() }
        val rows = LongArray(countMapFiltered.size) { i -> countMapFiltered[i].row.toLong() }

        return NativeManager.ndManager.createCoo(
            data, arrayOf(rows, cols),
            Shape(input.size.toLong(), vectorization.size.toLong())
        )
    }
}*/
// https://github.com/londogard/londogard-nlp-toolkit/commit/e988168374df53c763b1f814db59a1ab1717ef25#diff-7faa8eeec24df277b8ea6fd4a6645c64fe162be7a7453fece8ede910c9cc2d07