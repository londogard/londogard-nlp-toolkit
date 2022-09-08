package com.londogard.nlp.machinelearning.transformer

import com.londogard.nlp.meachinelearning.predictors.transformers.ClassifierPipeline
import com.londogard.nlp.meachinelearning.predictors.transformers.TokenClassificationPipeline
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper
import com.londogard.nlp.utils.huggingface.Engine
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.Test
import java.nio.file.Paths

class TransformerPipelineTest {
    @Test
    fun testClassifierPipelineLoadTorchScriptByPath() {
        // this model simply predict 50/50
        val jitTorchScriptPipeline = ClassifierPipeline.fromPath(
            Paths.get(javaClass.getResource("/traced_sgugger/tiny-distilbert-classification.pt")!!.path),
            mapOf(),
            HuggingFaceTokenizerWrapper("sgugger/tiny-distilbert-classification"),
            Engine.PYTORCH
        )
        jitTorchScriptPipeline.use { pipeline ->
            pipeline.predict("I love this awesome project!") shouldBeEqualTo "0"
        }
    }

    @Test
    fun testClassifierPipelineLoadONNXByPath() {
        // this model simply predict 50/50
        val onnxPipeline = ClassifierPipeline.fromPath(
            Paths.get(javaClass.getResource("/onnx/model.onnx")!!.path),
            mapOf(),
            HuggingFaceTokenizerWrapper("sgugger/tiny-distilbert-classification"),
            Engine.ONNX
        )
        onnxPipeline.use { pipeline ->
            pipeline.predict("I love this awesome project!") shouldBeEqualTo "1"
        }
    }

    // @Test don't run in CI/CD
    fun testClassifierPipelineONNXByHuggingFaceHub() {
        ClassifierPipeline
            .create("optimum/distilbert-base-uncased-finetuned-sst-2-english")
            .use { pipeline ->
                pipeline.predict("I love this awesome project!") shouldBeEqualTo "POSITIVE"
            }
    }

    // @Test don't run in CI/CD
    fun testTokenClassifierPipelineONNXByHuggingFaceHub() {
        TokenClassificationPipeline
            .create("optimum/bert-base-NER")
            .use { pipeline ->
                val result = pipeline.predict("My name is Hampus, I live in Sweden.")

                result shouldContainAll listOf("B-PER", "B-LOC")
            }
    }
}