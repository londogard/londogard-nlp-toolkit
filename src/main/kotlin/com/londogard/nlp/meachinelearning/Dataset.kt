package com.londogard.nlp.meachinelearning

import org.jetbrains.kotlinx.multik.ndarray.data.D2Array

class Dataset(val X: D2Array<Float>, val y: D2Array<Float>) {
    val numFeatures by lazy { X.shape[1] }
    val numSamples by lazy { X.shape[0] }
}