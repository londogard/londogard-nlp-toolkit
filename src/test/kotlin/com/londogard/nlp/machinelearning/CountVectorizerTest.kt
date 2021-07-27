package com.londogard.nlp.machinelearning

import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDManager
import ai.djl.ndarray.types.Shape
import ai.djl.ndarray.types.SparseFormat
import com.londogard.nlp.meachinelearning.vectorizer.CountVectorizer
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import space.kscience.kmath.ejml.EjmlLinearSpaceFDRM.buildMatrix
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.toList
import kotlin.system.measureNanoTime
import kotlin.test.Test


class CountVectorizerTest {
    @Test
    fun testSimpleVec() {
        measureNanoTime {
            repeat(1000) {
                val mat = buildMatrix(5000, 100) { i, j -> if ((i + j) % 100 == 0) 1f else 0f }.as2D()
                CountVectorizer<Float>().fitTransform(mat.as2D()).rows.map { it.toList() }
            }
        }.also { println(it / 10e3 / 1000) }
    }

    @Test
    fun testMultikVec() {
        measureNanoTime {
            repeat(1000) {
                val vect = CountVectorizer<Float>()
                val mat = mk.d2array(5000, 100) { i -> if (i % 100 == 0) 1f else 0f }
                vect.fit(mat)
                vect.transform(mat)
            }
        }.also { println(it / 10e3 / 1000) }
    }

    @Test
    fun testNDArrayVec() {
        measureNanoTime {
            repeat(1000) {
                NDManager.newBaseManager()
                    .use { manager ->
                        val vectorizer = CountVectorizer<Float>()
                        val mat = manager.create(IntArray(5000 * 100) { i -> if (i % 100 == 0) 1 else 0 }, Shape(5000, 100))
                        vectorizer.fit(mat)
                        vectorizer.transform(mat, manager).toIntArray().toList()
                    }
            }
        }.also { println(it / 10e3 / 1000) }
    }
}
/**
 * 12864    303     611
 * 12452    378     834
 * 8446     466     893
 */