package com.londogard.nlp.machinelearning

import com.londogard.nlp.meachinelearning.datasets.prepare.PennTreeBankPreparer
import com.londogard.nlp.meachinelearning.predictors.sequence.HiddenMarkovModel
import org.amshove.kluent.shouldBeEqualTo
import kotlin.test.Test

class SequenceClassifierTest {
    @Test
    fun testHMM() {
        val text = """In	IN
an	DT
Oct.	NNP
19	CD
review	NN
of	IN
``	``
The	DT
Misanthrope	NN
''	''
at	IN
Chicago	NNP
's	POS
Goodman	NNP
Theatre	NNP
(	(
``	``
Revitalized	VBN
Classics	NNS
Take	VBP
the	DT
Stage	NN
in	IN
Windy	NNP
City	NNP"""
        val pennTreeBankDataset = PennTreeBankPreparer.prepare(text)
        val hmm = HiddenMarkovModel.fromPennTreebank(pennTreeBankDataset = pennTreeBankDataset)

        // use the reverse map to get the true labels (string) rather than int
        hmm.predict(pennTreeBankDataset.trainDataset.first) shouldBeEqualTo pennTreeBankDataset.trainDataset.second
    }
}