package com.londogard.nlp.meachinelearning.transformers

import space.kscience.kmath.nd.Structure2D

interface BaseTransformer<INPUT: Number, OUTPUT: Number> {
    fun transform(input: Structure2D<INPUT>): Structure2D<OUTPUT>
}