package com.londogard.nlp.meachinelearning.datasets.prepare

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

object PennTreeBankPreparer {
    data class PennTreeBankDataset(
        val tokenIndexing: Map<String, Int>,
        val tagIndexing: Map<String, Int>,
        val trainDataset: Pair<List<D1Array<Int>>, List<D1Array<Int>>>,
//        val testDataset: Pair<List<D1Array<Int>>, List<D1Array<Int>>>
    ) {
        val reverseTagIndexing by lazy { tagIndexing.entries.associate { it.value to it.key } }
        val reverseTokenIndexing by lazy { tokenIndexing.entries.associate { it.value to it.key } }
    }

    /**
     * Prepares the standard format of
     *    Oct.	NNP
     *    19	CD
     *    ..
     *
     * Into a usable format for our Sequence Classifiers
     * */
    fun prepare(text: String, delimeter: Char = '\t'): PennTreeBankDataset {
        val (tokens, tags) = text
            .split('\n', delimeter)
            .windowed(2, 2) { (a, b) -> a to b }
            .unzip()
        val tokenIndices = tokens.toSet().withIndex().associate { it.value to it.index }
        val tagIndices = (tags + "BOS").toSet().withIndex().associate { it.value to it.index }
        val X = listOf(mk.ndarray(tokens.map(tokenIndices::getValue).toIntArray()))
        val y = listOf(mk.ndarray(tags.map(tagIndices::getValue).toIntArray()))

        return PennTreeBankDataset(tokenIndices, tagIndices, X to y)
    }
}