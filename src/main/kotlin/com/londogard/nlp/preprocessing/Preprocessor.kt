package com.londogard.nlp.preprocessing

import com.londogard.nlp.tokenizer.TokenizerSpecialTokens.NumberPattern
import com.londogard.nlp.tokenizer.TokenizerSpecialTokens.NumberStr

object Preprocessor {
    fun replaceNumber(text: String): String = NumberPattern.replace(text, NumberStr)
    fun replaceAllCaps(text: String): String = TODO("")

    // Twitter related functions -- Might be removed (!)
    fun String.removeTickers() = replace(Regex("\\\$\\w*"), "")
    fun String.removeRTs() = replace(Regex("^RT[\\s]+"), "")
    fun String.removeURLs() = replace(Regex("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"), "")
    fun String.removeHashtags() = replace("#", "")
    fun String.removeMentions() = replace(Regex("[@#][\\w_-]+"), "")
    fun String.removeXMLEncodings() = replace(Regex("&[a-z]*;")," ")
    fun String.removeExtraSpaces() = replace(Regex("\\s+")," ")
}