package com.londogard.nlp.machinelearning

import ai.djl.ndarray.NDManager
import ai.djl.ndarray.types.Shape
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.transformers.numElements
import com.londogard.nlp.meachinelearning.vectorizer.CountVectorizer
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import space.kscience.kmath.ejml.EjmlLinearSpaceFDRM.buildMatrix
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.as2D
import kotlin.system.measureNanoTime
import kotlin.test.Test


class CountVectorizerTest {
    val cols = 10
    val rows = 5

    @Test
    fun testSimpleVec() {
        val mat = buildMatrix(rows, cols) { i, j -> if (i+j==0) 100f else (i.toFloat() + j) % 4 }
        val countMatrix = CountVectorizer<Float>().fitTransform(mat.rows)
        countMatrix[0,0] shouldBeEqualTo 1f
        countMatrix[0,1] shouldBeEqualTo 3f
        countMatrix[2,0] shouldBeEqualTo 0f          // validate that sparse matrix still return 0:s
        countMatrix.numElements() shouldBeEqualTo 21 // validate it's sparse
        countMatrix.shape.fold(1, Int::times) shouldBeEqualTo 25 // validate that shape is correct
    }


}