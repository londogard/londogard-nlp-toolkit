package com.londogard.nlp.embeddings

import org.nd4j.linalg.api.ops.impl.transforms.Cholesky
import org.nd4j.linalg.api.ops.impl.transforms.custom.Svd
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val d = Nd4j.createFromArray(arrayOf(
            arrayOf(19f,-3f,-3f),
            arrayOf(-3f,19f,15f),
            arrayOf(-3f,15f,13f),
        ))

        val c = Cholesky(d)
        c.sameDiff
        println(Nd4j.getExecutioner().exec(c).toList())
        Transforms

    }
}