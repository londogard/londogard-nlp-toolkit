package com.londogard.nlp.meachinelearning

import org.ejml.UtilEjml
import org.ejml.sparse.csc.mult.ImplMultiplication_FSCC
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.MutableMultiArray

abstract class D2MultikWrapper(val matrix: D2Array<Float>) : MutableMultiArray<Float, D2> by matrix {
    fun asSparse(): D2SparseArray {
        // return D2SparseArray(matrix, 0..shape[0], 0..shape[1])
        TODO("")
    }
}

/** Compressed Column (CC) Sparse Matrix File Format */
class D2SparseArray(matrix: D2Array<Float>, indices: List<Pair<Int, Int>>) : D2MultikWrapper(matrix) {
    val xIndices: IntArray = indices.unzip().first.toIntArray()

//    loat[] x = UtilEjml.adjust(gx, A.numRows)
//    var w = UtilEjml.adjust(gw, A.numRows, A.numRows)
//
//    C.growMaxLength(A.nz_length + B.nz_length, false)
//    C.indicesSorted = false
//    C.nz_length = 0
//
//    // C(i,j) = sum_k A(i,k) * B(k,j)
//
//    // C(i,j) = sum_k A(i,k) * B(k,j)
//    var idx0: Int = B.col_idx.get(0)
//    for (bj in 1 .. B.numCols)
//    {
//        val colB: Int = bj - 1
//        val idx1: Int = B.col_idx.get(bj)
//        C.col_idx.get(bj) = C.nz_length
//        if (idx0 == idx1) {
//            continue
//        }
//
//        // C(:,j) = sum_k A(:,k)*B(k,j)
//        for (bi in idx0 until idx1) {
//            val rowB: Int = B.nz_rows.get(bi)
//            val valB: Float = B.nz_values.get(bi) // B(k,j)  k=rowB j=colB
//            ImplMultiplication_FSCC.multAddColA(A, rowB, valB, C, colB + 1, x, w)
//        }
//
//        // take the values in the dense vector 'x' and put them into 'C'
//        val idxC0: Int = C.col_idx.get(colB)
//        val idxC1: Int = C.col_idx.get(colB + 1)
//        for (i in idxC0 until idxC1) {
//            C.nz_values.get(i) = x.get(C.nz_rows.get(i))
//        }
//        idx0 = idx1
//    }
}