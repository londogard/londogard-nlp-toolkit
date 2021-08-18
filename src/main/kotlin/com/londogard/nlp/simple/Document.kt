package com.londogard.nlp.simple

data class Document(val text: String) {
    val tokens: List<String> = TODO("") // should contain pos_ is some cases
    val nounChunks: List<String> = TODO("")
    val entities: List<String> = TODO("")
}