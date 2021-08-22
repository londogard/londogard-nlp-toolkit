package com.londogard.nlp.meachinelearning

import kotlin.math.exp

fun sigmoidFast(x: Float): Float =
    when { // the exp grows unstable, and within these ranges it's still that value. TODO is branching more expensive than op?
        x < -10 -> 0f
        x > 10 -> 1f
        else -> 1 / (1 + exp(-x))
    }