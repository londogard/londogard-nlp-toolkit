package com.londogard.nlp.machinelearning

import ai.djl.ndarray.NDManager
import ai.djl.ndarray.types.Shape
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.vectorizer.CountVectorizer
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import space.kscience.kmath.ejml.EjmlLinearSpaceFDRM.buildMatrix
import space.kscience.kmath.nd.as2D
import kotlin.system.measureNanoTime
import kotlin.test.Test


class CountVectorizerTest {
    val numRepeat = 100
    val cols = 50000
    val rows = 500

    @Test
    fun testSimpleVec() {
        measureNanoTime {
            repeat(numRepeat) {
                val mat = buildMatrix(rows, cols) { i, j -> if ((i + j) % 100 == 0) 1f else 0f }
                CountVectorizer<Float>(maxCount = Count(rows * cols / 50)).fitTransform(mat.as2D())
            }
        }.also { println(it / 10e3 / 1000) }
    }

    @Test
    fun testMultikVec() {
        measureNanoTime {
            repeat(numRepeat) {
                val vect = CountVectorizer<Float>(maxCount = Count(rows * cols / 50))
                val mat = mk.d2array(rows, cols) { i -> if (i % 100 == 0) 1f else 0f }
                vect.fit(mat)
                vect.transform(mat)
            }
        }.also { println(it / 10e3 / 1000) }
    }

    @Test
    fun testNDArrayVec() {
        measureNanoTime {
            repeat(numRepeat) {
                NDManager.newBaseManager()
                    .use { manager ->
                        val vectorizer = CountVectorizer<Float>(maxCount = Count(rows * cols / 50))
                        val mat = manager.create(IntArray(cols * rows) { i -> if (i % 100 == 0) 1 else 0 }, Shape(rows.toLong(), cols.toLong()))
                        vectorizer.fit(mat)
                        vectorizer.transform(mat, manager)
                    }
            }
        }.also { println(it / 10e3 / 1000) }
    }
}
/**
 * 12864    303     611     4371
 * 12452    378     834     4958
 * 8446     466     893     4632
 */