package com.londogard.nlp.utils.huggingface

import com.londogard.nlp.utils.DownloadHelper
import com.londogard.nlp.utils.FileInfo
import com.londogard.nlp.utils.UrlProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mu.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.readText

/**
 * Internal util to load HuggingFace models from the Hub
 */
object HuggingFaceModelHub {
    private val logger = KotlinLogging.logger {}
    private const val baseUrl = "https://huggingface.co"
    private val basePath = UrlProvider.rootPath.resolve("huggingface")

    fun id2label(path: Path): Map<Int, String> {
        return Json
            .parseToJsonElement(path.readText())
            .jsonObject["id2label"]
            ?.jsonObject
            ?.entries
            ?.associateBy({ it.key.toInt() }, { it.value.jsonPrimitive.content })
            ?: mapOf<Int, String>().withDefault { i -> i.toString() }
    }

    fun downloadModel(name: String, engine: Engine = Engine.ONNX, revision: String = "main"): FileInfo {
        // equal to wget https://huggingface.co/distilbert-base-uncased/resolve/main/pytorch_model.bin
        assert(engine == Engine.ONNX) {
            when (engine) {
                Engine.PYTORCH -> logger.warn { "PyTorch models must be JIT TorchScript-model (DJL requirement). Do this by following https://huggingface.co/docs/transformers/main/en/serialization#torchscript" }
                else -> logger.warn { "TensorFlow models must be in SavedModel-format (DJL requirement). Do this by following https://docs.djl.ai/docs/tensorflow/how_to_import_tensorflow_models_in_DJL.html" }
            }
        }
        val url = "$baseUrl/$name/resolve/$revision"
        val path = basePath.resolve(name)

        val file = FileInfo(
            engine.modelName(),
            path.resolve(engine.modelName()),
            "$url/${engine.modelName()}",
            "HuggingFace model ($name)"
        )
        DownloadHelper.downloadFileIfMissing(file)
        DownloadHelper.downloadFileIfMissing(
            FileInfo(
                "config.json",
                path.resolve("config.json"),
                "$url/config.json",
                "HuggingFace config ($name)"
            )
        )

        return file
    }

    fun load(
        name: String,
        engine: Engine = Engine.ONNX,
        revision: String = "main"
    ): HFModel {
        val file = downloadModel(name, engine, revision)

        return HFModel(file.path)
    }
}