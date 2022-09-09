package com.londogard.nlp.meachinelearning.loss

import com.londogard.nlp.meachinelearning.dot
import com.londogard.nlp.meachinelearning.inplaceOp
import org.jetbrains.kotlinx.multik.api.math.log
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.divAssign
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import kotlin.math.ln

/** A Logistic Loss Function */
class LogisticLoss {

    //J = 1/m*sum(dot(-y,log(sigmoid(X*theta)))-dot(1-y,log(1-sigmoid(X*theta))));
    fun loss(yPred: D2Array<Float>, X: MultiArray<Float, D2>, yTransposed: D2Array<Float>, yNegTransposed: D2Array<Float>): Float {
        val lossVec = yNegTransposed.dot(mk.math.log(yPred)) - yTransposed.dot((1f - yPred).inplaceOp { ln(it) })

        //// loss. .add(L2(1e-2f).reguralize(weights, X.size(0).toInt()).toDouble())
        return mk.math.sum(lossVec) / X.shape[0]
    }

    //grad = 1/m*sum(( sigmoid(X*theta) - y).*X,1)';
    fun gradient(yPred: D2Array<Float>, XT: MultiArray<Float, D2>, y: D2Array<Float>): D2Array<Float> {
        // 1 / (1 + exp(-x))
        val main = (XT dot (yPred - y)).transpose() as D2Array<Float>
        main /= XT.shape[1].toFloat()

        return main
    }
}