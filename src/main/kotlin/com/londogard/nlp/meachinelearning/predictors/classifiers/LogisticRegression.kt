package com.londogard.nlp.meachinelearning.predictors.classifiers

import ai.djl.modality.nlp.preprocess.SimpleTokenizer
import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.loss.LogisticLoss
import com.londogard.nlp.meachinelearning.optimizer.GradientDescent
import com.londogard.nlp.meachinelearning.predictors.asAutoOneHotClassifier
import com.londogard.nlp.meachinelearning.sigmoidFast
import com.londogard.nlp.meachinelearning.toDense
import com.londogard.nlp.meachinelearning.vectorizer.count.CountVectorizer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.math.log
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import kotlin.math.ln
import kotlin.math.log
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class LogisticRegression(
    private val optimizer: GradientDescent = GradientDescent(1000, 0.01f, 1e-6f)
) : Classifier {
    private lateinit var weights: D2Array<Float>
    private lateinit var losses: D1Array<Float>

    override fun fit(X: MultiArray<Float, D2>, y: D2Array<Int>) {
        weights = mk.zeros(y.shape[1], X.shape[1])
        println("Optimizing")
        val (weightOut, lossesOut) = optimizer.optimize(LogisticLoss(), weights, X, y.asType())
        weights = weightOut
        losses = lossesOut
    }

    override fun predict(X: MultiArray<Float, D2>): D2Array<Int> {
        val proba = predictProba(X.toDense())

        return mk.d2array(X.shape[0], weights.shape[0]) { i -> if (proba.data[i] < 0.5f) 0 else 1 }
    }

    fun predictProba(X: MultiArray<Float, D2>): MultiArray<Float, D2> =
        (X dot weights.transpose()).inplaceOp(::sigmoidFast)
}

object ImdbSimpleTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val df = DataFrame.readCSV("/Users/londogard/git/londogard-nlp-toolkit/docs/samples/e2e/imdb_small.csv")
        val tokenizer = SimpleTokenizer()
        val dfUpdated = df.add {
            "tokens" from "review"<String>().map { tokenizer.tokenize(it) }
        }
        val vectorizer = CountVectorizer<Int>()
        var x = dfUpdated["tokens"].toList() as List<List<String>>
        var y = dfUpdated["sentiment"].toList() as List<String>
        x = x.take(500)
        y = y.take(500)

        var (xTrain, xValid) = x.take(400) to x.takeLast(100)
        var (yTrain, yValid) = y.take(400) to y.takeLast(100)
        var xTrainV = vectorizer.fitTransform(xTrain)
        var xValidV = vectorizer.transform(xValid)

        val classifier = LogisticRegression().asAutoOneHotClassifier<LogisticRegression, String>()
        val time = measureTimeMillis { classifier.fit(xTrainV, yTrain) }
        println("Time spent: $time")
    }
}