package com.londogard.nlp

import com.londogard.nlp.utils.CompressionUtil
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test

class CompressionUtilTests {
    @Test
    fun testGunzip() {
        val lines = CompressionUtil.gunzip(Paths.get(javaClass.getResource("/hej.txt.gz")?.path ?: ""))
            .bufferedReader()
            .readLines()
            .filter(String::isNotBlank)

        lines shouldHaveSize 1
        lines shouldContain "hej"
    }

    @Test
    fun testUncompressTarGz() {
        val lines = CompressionUtil.uncompressTarGz(
            Paths.get(javaClass.getResource("/hej.tar.gz")?.path ?: ""),
            Files.createTempDirectory("tmp")
        )
            .toFile()
            .readLines()
            .filter(String::isNotBlank)

        lines shouldHaveSize 1
        lines shouldContain "hej"
    }

}