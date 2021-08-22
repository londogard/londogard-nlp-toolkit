package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.predictors.regression.SimpleLinearRegression
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.linspace
import org.jetbrains.kotlinx.multik.api.mk
import kotlin.test.Test

class RegressionTest {
    @Test
    fun testLinearRegression() {
        val linearRegression = SimpleLinearRegression()
        val X = mk.linspace<Float>(0, 100).reshape(1, 50)
        val y = mk.linspace<Float>(0, 100).reshape(1, 50)

        linearRegression.fit(X, y)
        linearRegression.predict(X) shouldBeEqualTo y
    }
}