package samples
import com.londogard.nlp.utils.LanguageSupport
import com.londogard.nlp.wordfreq.WordFrequencies

fun testWordFreq() {
    val hej = WordFrequencies.wordFrequency("hej", LanguageSupport.sv)
    val och = WordFrequencies.wordFrequency("och", LanguageSupport.sv)

    println("WordFrequency of 'hej'=$hej and 'och'=$och")
}
