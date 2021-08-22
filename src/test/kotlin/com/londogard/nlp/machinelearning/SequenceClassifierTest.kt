package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.predictors.sequence.HiddenMarkovModel
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import kotlin.test.Test

class SequenceClassifierTest {
    @Test
    fun testHMM() {
        val text = """In	IN
an	DT
Oct.	NNP
19	CD
review	NN
of	IN
``	``
The	DT
Misanthrope	NN
''	''
at	IN
Chicago	NNP
's	POS
Goodman	NNP
Theatre	NNP
(	(
``	``
Revitalized	VBN
Classics	NNS
Take	VBP
the	DT
Stage	NN
in	IN
Windy	NNP
City	NNP"""
        val (tokensText, tagsText) = text
            .split('\n')
            .map {
                val (a, b) = it.split('\t')
                a to b
            }.unzip()
        val tokenMap = (tokensText).toSet().withIndex().associate { elem -> elem.value to elem.index }
        val tagMap = (tagsText + "BOS").toSet().withIndex().associate { elem -> elem.value to elem.index }
        val reversetagMap = tagMap.asIterable().associate { (key, value) -> value to key }
        val hmm = HiddenMarkovModel(
            tagMap.asIterable().associate { (key, value) -> value to key },
            tokenMap.asIterable().associate { (key, value) -> value to key },
            BegginingOfSentence = tokenMap.getOrDefault("BOS", 0)
        )

        val x = listOf(mk.ndarray(tokensText.mapNotNull(tokenMap::get).toIntArray()))
        val y = listOf(mk.ndarray(tagsText.mapNotNull(tagMap::get).toIntArray()))

        hmm.fit(x, y)
        // predict.map { t -> t.data.map { reversetagMap[it] } } to get the real labels!
        hmm.predict(x) shouldBeEqualTo y
    }
}