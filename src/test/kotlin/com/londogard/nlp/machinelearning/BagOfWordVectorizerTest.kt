package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.vectorizer.CountVectorizer
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import kotlin.test.Test

class BagOfWordVectorizerTest {
    @Test
    fun testSimpleVec() {
        CountVectorizer<Int>().fitTransform(mk.d2array(2, 2) { i -> i / 2 }) shouldBeEqualTo mk.ndarray(
            intArrayOf(
                2,
                0,
                0,
                2
            ), 2, 2
        )
    }
}