package com.londogard.nlp.meachinelearning.native

import ai.djl.ndarray.NDManager
import java.io.Closeable

object NativeManager: Closeable {
    val ndManager: NDManager by lazy { NDManager.newBaseManager() }

    override fun close() { ndManager.close() }
}