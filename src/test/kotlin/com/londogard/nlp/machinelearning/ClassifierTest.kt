package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.predictors.asAutoOneHotClassifier
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
    val simpleTexts = listOf(
        "hejsan jag älskar sverige",
        "hej vad bra det är i sverige",
        "jag älskar sverige",
        "norge är ett land i norden",
        "norge norden",
        "norge norden",
        "norge norden"
    )
        .map(simpleTok::split)
    val y = mk.ndarray(intArrayOf(1, 1, 1, 0, 0, 0, 0), 7, 1)

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
    fun logisticTest() {
        val labelsMap = mapOf(
            0 to "Bank Charges",
            1 to "Betting",
            2 to "Card fees",
            3 to "Food",
            4 to "Lifestyle",
            5 to "Loan",
            6 to "Reversal",
            7 to "Salary",
            8 to "Unknown",
            9 to "Utilities & Bills",
            10 to "Withdrawal"
        )

        val reversedLabelMap = labelsMap.map { it.value to it.key }.toMap()

        val (data, categories) = listOf(
            "Vat amount charges" to "Bank Charges",
            "Loan payment credit" to "Loan",
            "Salary for Aug" to "Salary",
            "Payment from betking" to "Betting",
            "Purchase from Shoprite" to "Food",
        ).unzip()
        val simpleTok = SimpleTokenizer()
        val xData = data.map(simpleTok::split)
        val yList = categories.map { category -> reversedLabelMap.getOrDefault(category, 0) }
        val y = mk.ndarray(yList)

        val tfidf = TfIdfVectorizer<Float>()
        val lr = LogisticRegression().asAutoOneHotClassifier()

        val transformedData = tfidf.fitTransform(xData)
        lr.fit(transformedData, y)

        lr.predictSimple(tfidf.transform(xData)) shouldBeEqualTo lr.predictSimple(transformedData)
        lr.predictSimple(transformedData) shouldBeEqualTo y
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