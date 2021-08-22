package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.predictors.classifiers.LogisticRegression
import com.londogard.nlp.meachinelearning.predictors.classifiers.NaiveBayes
import com.londogard.nlp.meachinelearning.toDense
import com.londogard.nlp.meachinelearning.vectorizer.TfIdfVectorizer
import com.londogard.nlp.tokenizer.SimpleTokenizer
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.first
import org.junit.Test

class ClassifierTest {
    val simpleTok = SimpleTokenizer()
    val simpleTexts = listOf("hejsan jag älskar sverige", "hej vad bra det är i sverige", "jag älskar sverige", "jag hatar norge", "norge hatar", "norge hatar", "norge hatar")
        .map(simpleTok::split)
    val y = mk.ndarray(intArrayOf(1,1,1,0,0, 0, 0), 7, 1)

    @Test
    fun testLogisticRegression() {
        val tfidf = TfIdfVectorizer<Float>()
        val lr = LogisticRegression()

        val out = tfidf.fitTransform(simpleTexts)
        lr.fit(out, y)

        lr.predict(out) shouldBeEqualTo y
        lr.predict(tfidf.transform(listOf(simpleTexts.first()))).first() shouldBeEqualTo 1
    }

    @Test
    fun testNaiveBayes() {
        val tfidf = TfIdfVectorizer<Float>()
        val naiveBayes = NaiveBayes()

        val out = tfidf.fitTransform(simpleTexts)
        naiveBayes.fit(out, y)

        naiveBayes.predict(out) shouldBeEqualTo y
        naiveBayes.predict(tfidf.transform(listOf(simpleTexts.first())).toDense()).first() shouldBeEqualTo 1
    }
}