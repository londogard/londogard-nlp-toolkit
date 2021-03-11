package com.londogard.nlp.information

/** TODO
class TfIdfVectorizer(
    data: List<List<Int>>,
    ngram_range: Pair<Int, Int> = Pair(1,1),
    maxDf: Float = 1f,
    minDf: Int = 1,
    maxFeatures: Int? = null,
    vocab: Set<String>? = null
) {
    lateinit var df: List<String>
    lateinit var tf: List<String>

    init {
        val bags = data.map(BagOfWordsVectorizer::bagOfWords)
        bags.reduce { acc, map -> acc + map }

    }
    fun vectorize(data: List<List<Int>>): List<DoubleArray>  {
        TODO("")
    }
}
*/
