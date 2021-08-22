package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.metrics.Metrics
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.reversed
import kotlin.test.Test

class MetricTest {
    @Test
    fun testAccuracy() {
        val simpleArray = mk.ndarray(intArrayOf(1,1,0,0),4,1)
        val simpleMediumArray = mk.ndarray(intArrayOf(1,0,1,0), 4, 1)

        Metrics.accuracy(simpleArray, simpleArray.clone()) shouldBeEqualTo 1.0
        Metrics.accuracy(simpleArray, simpleArray.reversed()) shouldBeEqualTo 0.0
        Metrics.accuracy(simpleArray, simpleMediumArray) shouldBeEqualTo 0.5
    }
}