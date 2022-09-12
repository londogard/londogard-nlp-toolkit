package com.londogard.nlp.utils.huggingface

/**
 * [Engine] wraps the different backends possible to use, currently supports [ONNX] & [PYTORCH].
 */
enum class Engine {
    ONNX, // Requires implementation("ai.djl.onnxruntime:onnxruntime-engine:{djlVersion}")
    PYTORCH; // Requires implementation("ai.djl.pytorch:pytorch-engine:{djlVersion}")
    // TODO: Add , TENSORFLOW > "tf_model.h5" > "TensorFlow";

    fun modelName(): String {
        return when (this) {
            ONNX -> "model.onnx"
            PYTORCH -> "pytorch_model.bin"
        }
    }

    fun djlEngineName(): String {
        return when (this) {
            ONNX -> "OnnxRuntime"
            PYTORCH -> "PyTorch"    // Requires a JIT TorchScript-model
        }
    }
}