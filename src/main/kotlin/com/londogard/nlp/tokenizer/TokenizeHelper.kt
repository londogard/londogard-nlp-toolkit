package com.londogard.nlp.tokenizer

object TokenizeHelper {

    // 1. replace all chars with a short
    // 2. start modifying

    // Trie both ways
    //  - 3+ splits perhaps?

    fun getTopNgram(
        top: Int,
        currTokens: Map<String, Short>,
        currMaxToken: Short,
        vocab: Map<List<Short>, Int>
    ): List<String> {
        //vocab
        //    .fold(emptyMap<Pair<Short, Short>, Short>()) {
//
        //    }


        vocab
            .flatMap { (key, count) ->
                key
                    .windowed(2)
                    .map { it to count }
                    .groupBy({ it.first }, { (_, count) -> count })
                    .mapValues { (_, value) -> value.sum() }
                    .entries
            }
        TODO("")
    }
}