package com.londogard.nlp.utils

import java.net.URL
import java.nio.file.Path

data class FileInfo(val filename: String, val path: Path, val url: String, val description: String) {
    fun toUrl(): URL = URL(url)
}
