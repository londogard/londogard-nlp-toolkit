package com.londogard.nlp.simple

// TODO potentially actually have things in continous array (multik)
data class Corpus(
    val documents: List<Document>,
    val pipeline: List<Stage>
) {


    companion object {
        fun fromDataFrame() {}
    }
}