package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.classifiers.LogisticRegression
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.Percent
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.vectorizer.TfIdfVectorizer
import com.londogard.nlp.tokenizer.SentencePieceTokenizer
import com.londogard.nlp.tokenizer.SimpleTokenizer
import com.londogard.nlp.utils.LanguageSupport
import org.amshove.kluent.shouldBeEqualTo
import org.ejml.data.MatrixType
import org.ejml.simple.SimpleMatrix
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.annotations.DataSchema
import org.jetbrains.dataframe.io.readCSV
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.junit.Test

class LogisticRegressionTest {
    @Test
    fun test() {
        val a = listOf("hejsan jag älskar sverige", "hej vad bra det är i sverige", "jag älskar sverige", "jag hatar norge", "norge hatar", "norge hatar", "norge hatar")
        val tok = SentencePieceTokenizer.fromLanguageSupportOrNull(LanguageSupport.sv) ?: return
        val tfidf = TfIdfVectorizer<Float>()
        val lr = LogisticRegression()

        val out = tfidf.fitTransform(a.map(tok::split).map { it.toTypedArray() }).toNDArray()
        val y = mk.ndarray(intArrayOf(1,1,1,0,0, 0, 0), 7, 1)
        lr.fit(out, y)

        lr.predict(out) shouldBeEqualTo y
    }

    fun SimpleMatrix.toNDArray(): D2Array<Float> = when (type) {
        MatrixType.FDRM -> mk.ndarray(fdrm.data, numRows(), numCols())
        MatrixType.FSCC -> {
            convertToDense()
            toNDArray()
        }
        else -> throw UnsupportedOperationException("ERROR")
    }

    @DataSchema
    interface Imdb {
        val review: String
        val sentiment: String
        val label: Int
    }

    fun <T> DataFrame<T>.trainTestSplit(train: PercentOrCount = Percent(0.7), shuffle: Boolean = false): Pair<DataFrame<T>, DataFrame<T>> {
        val df = if (shuffle) shuffled() else this
        val numTrain = train.toCount(nrow())

        return df[0 until numTrain] to df[numTrain..nrow()]
    }

    @Test
    fun testLarger() {
        val tok = SimpleTokenizer() //SentencePieceTokenizer.fromLanguageSupportOrNull(LanguageSupport.en)!!
        val df = DataFrame
            .readCSV(javaClass.getResource("/imdb.csv")!!)
            .add("label") { if (it["sentiment"] == "positive") 1 else 0 }
            .typed<Imdb>()
            .update { it.col(Imdb::review) }
            .with { tok.split(it) }
            .print()

        val vec = TfIdfVectorizer<Float>(minCount = Count(5), minDf = Count(2),ngramRange = 1..2)
//        val (train, test) = df.trainTestSplit()

    }
}