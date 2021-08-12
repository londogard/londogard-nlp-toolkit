package com.londogard.nlp.meachinelearning.vectorizer.native

import ai.djl.ndarray.types.Shape
import com.londogard.nlp.meachinelearning.native.NativeManager
import com.londogard.nlp.meachinelearning.native.NativeNDArray
import com.londogard.nlp.meachinelearning.inputs.Coordinate
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.utils.IterableExtensions.identityCount
import com.londogard.nlp.utils.IterableExtensions.ngrams
import java.nio.FloatBuffer

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
        TODO("Not yet implemented")
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
        // val rowCounts = countMapFiltered.groupingBy { it.row }.eachCount()
        //val rows = LongArray(input.size + 1)
        //for (i in 1 until input.size + 1) {
        //    val numElems = rowCounts.getOrDefault(i - 1, 0)
        //    rows[i] = rows[i - 1] + numElems
        //}
        val cols = LongArray(countMapFiltered.size) { i -> countMapFiltered[i].col.toLong() }


        val rows = LongArray(countMapFiltered.size) { i -> countMapFiltered[i].row.toLong() }
        // NativeManager.ndManager.createCoo(data, arrayOf(rows, cols), Shape(input.size.toLong(), vectorization.size.toLong()))
        // NativeManager.ndManager.createCSR(data, rows, cols, Shape(input.size.toLong(), vectorization.size.toLong()))
        return NativeManager.ndManager.createCoo(data, arrayOf(rows, cols), Shape(input.size.toLong(), vectorization.size.toLong()))
    }
}