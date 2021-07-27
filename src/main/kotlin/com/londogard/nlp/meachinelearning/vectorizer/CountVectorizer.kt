package com.londogard.nlp.meachinelearning.vectorizer

import com.londogard.nlp.meachinelearning.NotFitException
import com.londogard.nlp.meachinelearning.inputs.Count
import com.londogard.nlp.meachinelearning.inputs.PercentOrCount
import com.londogard.nlp.meachinelearning.transformers.efficientSparseBuildMatrix
import com.londogard.nlp.utils.MapExtensions.mergeReduce
import org.ejml.data.FMatrixSparseCSC
import space.kscience.kmath.ejml.EjmlFloatMatrix
import space.kscience.kmath.ejml.EjmlLinearSpaceFSCC
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.FloatField
import space.kscience.kmath.structures.asIterable

// Pipeline Object
// Document Object
// HashingBagOfWords (collisions)

class CountVectorizer<T : Number>(
    val minCount: PercentOrCount = Count(0),
    val maxCount: PercentOrCount = Count(Int.MAX_VALUE),
) : BaseVectorizer<T, Float> {
    private lateinit var vectorization: Map<T, Int>

    // TODO perhaps List<Vector> as input?
    override fun fit(input: Matrix<T>) {
        val countMap = input.rows
            .map { it.asIterable().groupingBy { it }.eachCount() }

        val totalCount = input.rowNum * input.colNum
        vectorization = mutableMapOf<T, Int>()
            .mergeReduce(countMap) { a, b -> a + b }
            .filter { (_, count) ->
                minCount.isLesserThan(count, totalCount) && maxCount.isGreatherThan(count, totalCount)
            }
            .keys
            .mapIndexed { index, t -> t to index }
            .toMap()
    }



    override fun transform(input: Structure2D<T>): Structure2D<Float> {
        if (!::vectorization.isInitialized) {
            throw NotFitException("BagOfWordsVectorizer must be 'fit' before calling 'transform'!")
        }

        val countMap = input.rows
            .map { it.asIterable().groupingBy { it }.eachCount().filterKeys { key -> key in vectorization } }
            .map { count -> count.mapKeys { (key, _) -> vectorization[key] ?: -1 } }
        val totalElems = countMap.sumOf { it.size }

        return efficientSparseBuildMatrix(input.rowNum, vectorization.size, totalElems) {
                i, j -> (countMap[i][j] ?: 0).toFloat()
        }
    }
}