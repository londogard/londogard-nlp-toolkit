package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.datatypes.Percent
import com.londogard.nlp.meachinelearning.datatypes.PercentOrCount
import com.londogard.nlp.meachinelearning.predictors.classifiers.LogisticRegression
import com.londogard.nlp.meachinelearning.vectorizer.TfIdfVectorizer
import com.londogard.nlp.tokenizer.SimpleTokenizer
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.junit.Test

class LogisticRegressionTest {
    @Test
    fun test() {
        val a = listOf("hejsan jag älskar sverige", "hej vad bra det är i sverige", "jag älskar sverige", "jag hatar norge", "norge hatar", "norge hatar", "norge hatar")
        val tok = SimpleTokenizer()
        val tfidf = TfIdfVectorizer<Float>()
        val lr = LogisticRegression()

        val out = tfidf.fitTransform(a.map(tok::split))
        val y = mk.ndarray(intArrayOf(1,1,1,0,0, 0, 0), 7, 1)
        lr.fit(out, y)

        lr.predict(out) shouldBeEqualTo y
    }

    @DataSchema
    data class Imdb(val review: String, val sentiment: String, val label: Int)

    fun <T> DataFrame<T>.trainTestSplit(train: PercentOrCount = Percent(0.7), shuffle: Boolean = false): Pair<DataFrame<T>, DataFrame<T>> {
        val df = if (shuffle) shuffled() else this
        val numTrain = train.toCount(nrow())

        return df[0 until numTrain] to df[numTrain..nrow()]
    }
}