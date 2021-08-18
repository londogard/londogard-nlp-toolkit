package com.londogard.nlp

private fun String.removeTickers() = replace(Regex("\\\$\\w*"), "")
private fun String.removeRTs() = replace(Regex("^RT[\\s]+"), "")
private fun String.removeURLs() = replace(Regex("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"), "")
private fun String.removeHashtags() = replace("#", "")
private fun String.removeMentions() = replace(Regex("[@#][\\w_-]+"), "")
private fun String.removeXMLEncodings() = replace(Regex("&[a-z]*;")," ")
private fun String.removeExtraSpaces() = replace(Regex("\\s+")," ")