package com.londogard.nlp.tokenizer

object TokenizerSpecialTokens {
    const val Pad: Char = '\u0001'               // START_OF_HEADING

    const val AllCaps: Char = '\u000E'           // SHIFT_OUT

    const val Number: Char = '\u000E'            // SHIFT_IN
    const val NumberStr: String = Number.toString()
    val NumberPattern = "\\d".toRegex()

    const val Upper: Char = '\u001b'             // ESCAPE
    const val BOS: Char = '\u0002'               // START_OF_TEXT
    const val EOS: Char = '\u0003'               // END_OF_TEXT
    const val Space: Char = ' '                  // SPACE
    const val StartOfWord: Char = '_'            // UNDERSCORE

    // Beginning of Word: Used in subword tokenizers
    const val BOW: Char = '_'                     // UNDERSCORE

    // Repetition used if 3 or more repeated
    const val WordRepetition: Char = '\u0011'    // DEVICE_CONTROL_ONE
    const val CharRepetition: Char = '\u0012'    // DEVICE_CONTROL_TWO
}