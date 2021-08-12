package com.londogard.nlp.meachinelearning.loss

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import com.londogard.nlp.meachinelearning.sigmoidFast
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.ln

class LogisticLoss: Loss {
    //J = 1/m*sum(dot(-y,log(sigmoid(X*theta)))-dot(1-y,log(1-sigmoid(X*theta))));
    override fun loss(weights: D2Array<Float>, X: MultiArray<Float, D2>, y: D2Array<Float>): Float {
        val sigmoidVector = (X dot weights.transpose()).inplaceOp(::sigmoidFast)
        val yNegTransposed = y.transpose() - 1f
        val lossVec = yNegTransposed.dot(sigmoidVector.map { ln(it) }) - (yNegTransposed + 1f).dot((1f - sigmoidVector).inplaceOp { ln(it) })

        //// loss. .add(L2(1e-2f).reguralize(weights, X.size(0).toInt()).toDouble())
        return lossVec.data.sum() / X.shape[0]
    }

    // TODO add L2/L1 regularization
    // TODO add potential Intercept

    //grad = 1/m*sum((sigmoid(X*theta)-y).*X,1)';
    override fun gradient(weights: D2Array<Float>, X: MultiArray<Float, D2>, y: D2Array<Float>): D2Array<Float> {
        val res = (X dot weights.transpose()).inplaceOp(::sigmoidFast).apply { (this as D2Array<Float>).minusAssign(y) }
        // aT*b = bT * a
        val main = (X.transpose() dot res).transpose() as D2Array<Float>
        main /= X.shape[0].toFloat()

        return main
    }
}