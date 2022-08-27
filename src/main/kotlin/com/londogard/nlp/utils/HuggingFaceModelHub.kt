package com.londogard.nlp.utils

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mu.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.readText


enum class Engine {
    ONNX, PYTORCH, TENSORFLOW;

    fun modelName(): String {
        return when (this) {
            ONNX -> "model.onnx"
            PYTORCH -> "pytorch_model.bin"
            TENSORFLOW -> "tf_model.h5"
        }
    }

    fun djlEngineName(): String {
        return when (this) {
            ONNX -> "OnnxRuntime"
            PYTORCH -> "PyTorch"
            /**
             * Requires to have JIT Scripted Torch Model
             */
            TENSORFLOW -> "TensorFlow"
            /**
             * Requires:
             * loaded_model = keras.models.load_model("resnet.h5"); tf.saved_model.save(loaded_model, "resnet/1/")
             */
        }
    }
}

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
            ?.associateBy({ it.key.toInt() }, { it.value.jsonPrimitive.toString() })
            ?: mapOf<Int, String>().withDefault { i -> i.toString() }
    }

    fun downloadModel(name: String, engine: Engine = Engine.ONNX, revision: String = "main"): FileInfo {
        // wget https://huggingface.co/distilbert-base-uncased/resolve/main/pytorch_model.bin
        assert(engine == Engine.ONNX) {
            when (engine) {
                Engine.PYTORCH -> logger.warn { "PyTorch models must be JIT Scripted Torch Model (DJL requirement). Do this by following https://huggingface.co/docs/transformers/main/en/serialization#torchscript" }
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

data class HFModel(val localPath: Path)