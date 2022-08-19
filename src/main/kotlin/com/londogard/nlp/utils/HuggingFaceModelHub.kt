package com.londogard.nlp.utils

import java.util.InputMismatchException

val S3_BUCKET_PREFIX = "https://s3.amazonaws.com/models.huggingface.co/bert"
val CLOUDFRONT_DISTRIB_PREFIX = "https://cdn.huggingface.co"
// "{model_id}/resolve/{revision}/{filename}"

enum class Engine {
    ONNX, PYTORCH, TENSORFLOW

    fun modelName(): String {
        return when (this) {
            ONNX -> "model.onnx"
            PYTORCH -> "pytorch_model.bin"
            TENSORFLOW -> "tf_model.h5"
        }
    }
}

object HuggingFaceModelHub {
    private const val baseUrl = "https://huggingface.co"

    fun downloadModel(name: String, engine: Engine = Engine.ONNX, revision: String = "main") {
        val url = "$baseUrl/$name/resolve/$revision/${engine.modelName()}"
        DownloadHelper.downloadFileIfMissing(FileInfo(engine.modelName(), ))

    // wget https://huggingface.co/distilbert-base-uncased/resolve/main/pytorch_model.bin
    }
}