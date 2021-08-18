package com.londogard.nlp.simple

interface Pipeline {
    val preprocessing: List<Stage>
    val tokenizing: Stage
    val preprocessTokens: List<Stage>
    val classifiers: List<Stage>
}