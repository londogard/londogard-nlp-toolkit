package com.londogard.nlp.meachinelearning.predictors.sequence

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import kotlin.math.ln

class HiddenMarkovModel(
    val labelMap: Map<Int, String> = emptyMap(),
    val inputMap: Map<Int, String> = emptyMap(),
    val alpha: Float = 0.001f,
    val BegginingOfSentence: Int = 0
) : SequenceClassifier<Int> {
    var NUMBER_OF_TAGS: Int = 0
    lateinit var transitionMatrix: D2Array<Float>
    lateinit var emissionMatrix: D2Array<Float>

    override fun fit(X: List<D1Array<Int>>, y: List<D1Array<Int>>) {
        require(X.size == y.size) { "It is required that X and y has exactly the same number of examples (X: ${X.size}, y: ${y.size})." }

        val transitionCounts = mutableMapOf<Pair<Int, Int>, Int>()
        val emissionCounts = mutableMapOf<Pair<Int, Int>, Int>() // TODO optimize by true array / init directly?
        val tagCounts = mutableMapOf<Int, Int>()

        // calculateCounts
        for (row in X.indices) {
            val (xSample, ySample) = X[row] to y[row]
            require(xSample.size == ySample.size) { "X and Y must have a 1-to-1 relationship, each input gets a class" }
            var previousTag = BegginingOfSentence

            for (i in xSample.indices) {
                val (transition, emission) = (previousTag to ySample[i]) to (ySample[i] to xSample[i])

                transitionCounts[transition] = 1 + transitionCounts.getOrDefault(transition, 0)
                emissionCounts[emission] = emissionCounts.getOrDefault(emission, 0) + 1
                tagCounts[ySample[i]] = tagCounts.getOrDefault(ySample[i], 0) + 1
                previousTag = ySample[i]
            }
        }

        // === buildTransitionMatrix ===
        NUMBER_OF_TAGS = tagCounts.keys.size + 1
        transitionMatrix = mk.zeros(NUMBER_OF_TAGS, NUMBER_OF_TAGS)

//      Go through each row and column of the transition matrix
        for (i in 0 until NUMBER_OF_TAGS)   // TODO probably simpler to create this matrix from get-go then scale it
            for (j in 0 until NUMBER_OF_TAGS) {

                val transition = i to j

//          If the (prev POS tag, current POS tag) exists in the transition counts dictionary, change the count
                val count = transitionCounts.getOrDefault(transition, 0)

//          Get the count of the previous tag (index position i) from tag counts
                val countPrevTag = tagCounts[i]

//          Apply smoothing to avoid numeric underflow
                transitionMatrix[i, j] = (count + alpha) / (alpha * NUMBER_OF_TAGS + (countPrevTag ?: 0))
            }

        // === Create emissionmatrix ===
        val NUMBER_OF_TOKENS = emissionCounts.keys.map { (_, token) -> token }.toSet().size // BOS
        emissionMatrix = mk.zeros(NUMBER_OF_TAGS, NUMBER_OF_TOKENS)

        for (tag in 0 until NUMBER_OF_TAGS)
            for (token in 0 until NUMBER_OF_TOKENS) {

                val emission = tag to token
                val count = emissionCounts.getOrDefault(emission, 0)
                val countTag = tagCounts[tag]
                emissionMatrix[tag, token] = (count + alpha) / (alpha * NUMBER_OF_TOKENS + (countTag ?: 0))
            }
    }

    override fun predict(X: List<D1Array<Int>>): List<D1Array<Int>> {
        require(NUMBER_OF_TAGS > 0) { "Need to run 'fit' before 'predict'!" }
        return X
            .map { xSample ->
                // Initialize Viterbi Matrix
                val bestProbs = mk.zeros<Float>(NUMBER_OF_TAGS, xSample.size)
                val bestPaths = mk.zeros<Int>(NUMBER_OF_TAGS, xSample.size)

                val startIdx = BegginingOfSentence // TODO validate this please!

                // populating the first column of the bestProbs to initialize it
                for (i in 0 until NUMBER_OF_TAGS) {
                    if (transitionMatrix[0, i] == 0f) {
                        bestProbs[i, 0] = Float.NEGATIVE_INFINITY
                    } else {
                        bestProbs[i, 0] = ln(transitionMatrix[startIdx, i]) + ln(emissionMatrix[i, xSample[0]])
                    }
                }

                // === viterbiForward ===
                for (i in 1 until xSample.size)
                    for (j in 0 until NUMBER_OF_TAGS) {

                        var bestProbabilityToGetToWordIFromTagJ = Float.NEGATIVE_INFINITY
                        var bestPathToWordI = 0

                        for (k in 0 until NUMBER_OF_TAGS) {

                            val temp_prob =
                                bestProbs[k, i - 1] + ln(transitionMatrix[k, j]) + ln(emissionMatrix[j, xSample[i]])

                            if (temp_prob > bestProbabilityToGetToWordIFromTagJ) {
                                bestProbabilityToGetToWordIFromTagJ = temp_prob
                                bestPathToWordI = k
                            }
                        }
                        bestProbs[j, i] = bestProbabilityToGetToWordIFromTagJ
                        bestPaths[j, i] = bestPathToWordI
                    }

                // viterbiBackward
                val m = xSample.size
                val z = IntArray(m)

                // Find highest prob of final column
                z[m - 1] = mk.math.argMax(bestProbs.view(bestProbs.shape[1] - 1, 1))  // INDEX == TAG

                //      traversing the bestPaths backwards.
                //      each current cell contains the row index of the cell to go to in the next column
                for (i in m - 1 downTo 1) {
                    val tagForWordI = bestPaths[z[i], i]
                    z[i - 1] = tagForWordI
                }

                mk.ndarray(z)
            }

    }
}