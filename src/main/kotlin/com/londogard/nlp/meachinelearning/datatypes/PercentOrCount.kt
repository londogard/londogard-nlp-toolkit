package com.londogard.nlp.meachinelearning.datatypes

sealed class PercentOrCount {
    abstract fun <T: Number> isGreatherThan(other: T, totalCount: Int): Boolean
    abstract fun <T: Number> isLesserThan(other: T, totalCount: Int): Boolean
    abstract fun isEq(other: Int): Boolean
    abstract fun toCount(totalCount: Int): Int
}

data class Percent(val percent: Double) : PercentOrCount() {
    override fun <T : Number> isGreatherThan(other: T, totalCount: Int) = percent > other.toDouble() / totalCount
    override fun <T : Number> isLesserThan(other: T, totalCount: Int) = percent < other.toDouble() / totalCount
    override fun isEq(other: Int): Boolean = percent == other.toDouble()
    override fun toCount(totalCount: Int): Int = (percent * totalCount).toInt()
}

data class Count(val number: Int) : PercentOrCount() {
    override fun <T : Number> isGreatherThan(other: T, totalCount: Int) = number > other.toInt()
    override fun <T : Number> isLesserThan(other: T, totalCount: Int) = number < other.toInt()
    override fun isEq(other: Int): Boolean = number == other
    override fun toCount(totalCount: Int): Int = number
}
