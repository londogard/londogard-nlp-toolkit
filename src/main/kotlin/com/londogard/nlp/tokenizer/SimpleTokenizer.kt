package com.londogard.nlp.tokenizer

import java.util.*
import java.util.regex.Pattern

/**
 * A simple tokenizer which allows you to define your own whitespace to split upon.
 */
class SimpleTokenizer(
    private val splitContraction: Boolean = false,
    private val whitespaceRegex: String = WHITESPACE
) : Tokenizer {
    override fun split(text: String): List<String> {
        val whitespacePattern = Pattern.compile(whitespaceRegex)
        var updatedText = text
        if (splitContraction) {
            updatedText = WONT_CONTRACTION.matcher(updatedText).replaceAll("$1ill not")
            updatedText = SHANT_CONTRACTION.matcher(updatedText).replaceAll("$1ll not")
            updatedText = AINT_CONTRACTION.matcher(updatedText).replaceAll("$1m not")
            for (regexp in NOT_CONTRACTIONS) {
                updatedText = regexp.matcher(updatedText).replaceAll("$1 not")
            }
            for (regexp in CONTRACTIONS2) {
                updatedText = regexp.matcher(updatedText).replaceAll("$1 $2")
            }
            for (regexp in CONTRACTIONS3) {
                updatedText = regexp.matcher(updatedText).replaceAll("$1 $2 $3")
            }
        }
        updatedText = DELIMITERS[0].matcher(updatedText).replaceAll(" $1 ")
        updatedText = DELIMITERS[1].matcher(updatedText).replaceAll(" $1")
        updatedText = DELIMITERS[2].matcher(updatedText).replaceAll(" $1")
        updatedText = DELIMITERS[3].matcher(updatedText).replaceAll(" . ")
        updatedText = DELIMITERS[4].matcher(updatedText).replaceAll(" $1 ")
        val words = whitespacePattern.split(updatedText)

        val result = ArrayList<String>()
        for (token in words) {
            if (token.isNotEmpty()) {
                result.add(token)
            }
        }
        return result
    }

    companion object {
        private val WONT_CONTRACTION: Pattern = Pattern.compile("(?i)\\b(w)(on't)\\b")
        private val SHANT_CONTRACTION: Pattern = Pattern.compile("(?i)\\b(sha)(n't)\\b")
        private val AINT_CONTRACTION: Pattern = Pattern.compile("(?i)\\b(a)(in't)\\b")
        private val NOT_CONTRACTIONS: Array<Pattern> = arrayOf(
            Pattern.compile("(?i)\\b(can)('t|not)\\b"),
            Pattern.compile("(?i)(.)(n't)\\b")
        )

        /**
         * List of contractions adapted from Robert MacIntyre's tokenizer.
         */
        private val CONTRACTIONS2: Array<Pattern> = arrayOf(
            Pattern.compile("(?i)(.)('ll|'re|'ve|'s|'m|'d)\\b"),
            Pattern.compile("(?i)\\b(D)('ye)\\b"),
            Pattern.compile("(?i)\\b(Gim)(me)\\b"),
            Pattern.compile("(?i)\\b(Gon)(na)\\b"),
            Pattern.compile("(?i)\\b(Got)(ta)\\b"),
            Pattern.compile("(?i)\\b(Lem)(me)\\b"),
            Pattern.compile("(?i)\\b(Mor)('n)\\b"),
            Pattern.compile("(?i)\\b(T)(is)\\b"),
            Pattern.compile("(?i)\\b(T)(was)\\b"),
            Pattern.compile("(?i)\\b(Wan)(na)\\b")
        )
        private val CONTRACTIONS3: Array<Pattern> = arrayOf(
            Pattern.compile("(?i)\\b(Whad)(dd)(ya)\\b"),
            Pattern.compile("(?i)\\b(Wha)(t)(cha)\\b")
        )
        private val DELIMITERS: Array<Pattern> =
            arrayOf(
                Pattern.compile("((?U)[^\\w.'\\-/,&<>:])"),  // Separate most punctuation
                Pattern.compile("(?U)(,\\s)"),  // Separate commas if they're followed by space (e.g., don't separate 2,500)
                Pattern.compile("(?U)('\\s)"),  // Separate single quotes if they're followed by a space.
                Pattern.compile("(?U)\\. *(\\n|$)"),  // Separate periods that come before newline or end of string.
                Pattern.compile("(?U)(\\.{3,})")  // Separate continuous periods such as ... in ToC.
            )
        private const val WHITESPACE: String = "[^\\S\\r\\n]+"
    }
}
