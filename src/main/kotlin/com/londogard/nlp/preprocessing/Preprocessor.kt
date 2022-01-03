package com.londogard.nlp.preprocessing

import com.londogard.nlp.tokenizer.TokenizerSpecialTokens.NumberPattern
import com.londogard.nlp.tokenizer.TokenizerSpecialTokens.NumberStr

object Preprocessor {
    fun replaceNumber(text: String): String = NumberPattern.replace(text, NumberStr)

    // Twitter related functions -- Might be removed (!)
    fun String.removeTickers(): String = TickerPattern.replace(this, "")
    fun String.removeRTs(): String = RetweetPattern.replace(this, "")
    fun String.removeURLs(): String = URLPattern.replace(this, "")
    fun String.removeHashtags(): String = replace("#", "")
    fun String.removeMentions(): String = MentionPattern.replace(this, "")
    fun String.removeXMLEncodings(): String = XMLPattern.replace(this, " ")
    fun String.removeExtraSpaces(): String = WhiteSpacePattern.replace(this, " ")

    private val TickerPattern by lazy { Regex("\\\$\\w*") }
    private val RetweetPattern by lazy { Regex("^RT[\\s]+") }
    private val MentionPattern by lazy { Regex("[@#][\\w_-]+") }
    private val URLPattern by lazy { Regex("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]") }
    private val XMLPattern by lazy { Regex("&[a-z]*;") }
    private val WhiteSpacePattern by lazy { Regex("\\s+") }
}