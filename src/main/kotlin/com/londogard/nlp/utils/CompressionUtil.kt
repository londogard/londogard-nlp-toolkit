package com.londogard.nlp.utils

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.GZIPInputStream

object CompressionUtil {
    fun gunzip(path: Path): InputStream =
        path.toFile()
            .inputStream()
            .let(::GZIPInputStream)

    @Throws(IOException::class)
    fun uncompressTarGz(input: Path, dest: Path = input.parent): Path {
        return Files
            .newInputStream(input)
            .let(::BufferedInputStream)
            .let(::GzipCompressorInputStream)
            .let(::TarArchiveInputStream)
            .use { tarArchive ->
                var entry: ArchiveEntry?
                var path: Path = dest

                while (tarArchive.nextEntry.also { entry = it } != null) {
                    // create a new path, remember check zip slip attack
                    val newPath: Path = zipSlipProtect(entry!!, dest)

                    if (path == dest) path = newPath
                    if (entry?.isDirectory == true) {
                        Files.createDirectories(newPath)
                    } else {
                        // check parent folder again
                        Files.createDirectories(newPath.parent)

                        Files.copy(tarArchive, newPath, StandardCopyOption.REPLACE_EXISTING)
                    }
                }

                path
            }
    }

    @Throws(IOException::class)
    private fun zipSlipProtect(entry: ArchiveEntry, targetDir: Path): Path {
        val targetDirResolved = targetDir.resolve(entry.name)

        // make sure normalized file still has targetDir as its prefix, else throws exception
        val normalizePath = targetDirResolved.normalize()
        if (!normalizePath.startsWith(targetDir)) {
            throw IOException("Bad entry: " + entry.name)
        }

        return normalizePath
    }
}