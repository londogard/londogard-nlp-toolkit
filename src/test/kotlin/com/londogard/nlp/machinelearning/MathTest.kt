package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.sum
import com.londogard.nlp.meachinelearning.toDense
import com.londogard.nlp.meachinelearning.toSparse
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import kotlin.test.Test

class MathTest {
    @Test
    fun testSparseDot() {
        val A = mk.d2array(2,2) { it.toFloat() }
        val B = mk.d2array(2,2) { (it % 2).toFloat() }
        val res = A dot B
        val resSparse = A.toSparse() dot B.toSparse()
        val respSparseDense = A.toSparse() dot B
        val respDenseSparse = A dot B.toSparse()

        res shouldBeEqualTo resSparse.toDense()
        respSparseDense shouldBeEqualTo res
        respDenseSparse shouldBeEqualTo res
    }

    @Test
    fun testSparseSum() {
        val A = mk.d2array(2,2) { it.toFloat() }
        A.sum() shouldBeEqualTo A.toSparse().sum()

        // there is a bug in Multik swapping the axises
        mk.math.sumD2(A, axis = 1) shouldBeEqualTo A.toSparse().sum(byRow=true)
    }
}