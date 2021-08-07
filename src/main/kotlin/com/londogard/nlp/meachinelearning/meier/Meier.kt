package com.londogard.nlp.meachinelearning.meier

import kotlin.math.cos
import kotlin.math.sin

class DN(val r: Float, val eps: List<Float>)

fun sin(x: DN): DN = DN(sin(x.r), cos(x.r) * x.eps)
fun cos(x: DN): DN = DN(cos(x.r), -sin(x.r)*x.eps)
operator fun DN.times(that: DN): DN =
    DN(this.r * that.r, eps = this.eps * that.r + this.r * that.eps)
operator fun DN.plus(that: DN): DN = DN(this.r+that.r, this.eps+that.eps)
operator fun DN.minus(that: DN): DN = DN(this.r - that.r, this.eps - that.eps)
operator fun DN.unaryMinus(): DN = DN(-this.r, -this.eps)
operator fun DN.div(that: DN): DN = (this.r/that.r).let{ DN(it, this.eps/that.r - it*that.eps/that.r ) }
operator fun Float.times(that: List<Float>) = that.map { this * it }

operator fun List<Float>.times(that: Float) = this.map { it*that }
operator fun List<Float>.unaryMinus() = this.map { -it }
operator fun List<Float>.plus(that: List<Float>) = zip(that) {x,y -> x+y}
operator fun List<Float>.minus(that: List<Float>) = zip(that) {x,y -> x-y}
operator fun List<Float>.div(that: Float) = this.map { it/that }



