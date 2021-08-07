package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.jvm.JvmLinAlg
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array

fun D2Array<Float>.inplaceOp(op: (Float) -> Float): D2Array<Float> = apply {
    for (i in data.indices) {
        data[i] = op(data[i])
    }
}

fun D2Array<Float>.sigmoid() = inplaceOp(::sigmoidFast)

infix fun <T: Number> D2Array<T>.dot(other: D2Array<T>) = JvmLinAlg.dot(this, other)