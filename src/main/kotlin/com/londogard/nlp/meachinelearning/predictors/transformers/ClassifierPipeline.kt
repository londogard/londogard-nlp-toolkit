package com.londogard.nlp.meachinelearning.predictors.transformers

import ai.djl.Device
import ai.djl.repository.zoo.Criteria
import com.londogard.nlp.meachinelearning.predictors.transformers.translators.ClassifierTranslator
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper
import com.londogard.nlp.tokenizer.Tokenizer
import com.londogard.nlp.utils.Engine
import com.londogard.nlp.utils.HuggingFaceModelHub
import java.nio.file.Path
import kotlin.io.path.Path

class ClassifierPipeline(model: Criteria<String, String>) : TransformerPipeline<String, String>(model) {
    companion object {
        fun fromPath(
            modelPath: Path,
            id2label: Map<Int, String>,
            tokenizer: Tokenizer,
            engine: Engine,
            device: Device = Device.cpu()
        ): ClassifierPipeline {
            assert (tokenizer is HuggingFaceTokenizerWrapper) {
                "Current implementation requires HuggingFace tokenizers to be used"
            }
            val translator = ClassifierTranslator(tokenizer as HuggingFaceTokenizerWrapper, id2label)
            val criteria = baseCriteria(translator, modelPath, engine, device)

            return ClassifierPipeline(criteria)
        }

        fun create(
            modelName: String,
            engine: Engine = Engine.ONNX,
            device: Device = Device.cpu(),
        ): ClassifierPipeline {
            val model = HuggingFaceModelHub.load(modelName, engine)
            val configPath = model.localPath.parent.resolve("config.json")
            val config = HuggingFaceModelHub.id2label(configPath)
            val translator = ClassifierTranslator(HuggingFaceTokenizerWrapper(modelName), config)
            val criteria = baseCriteria(translator, model.localPath, engine, device)

            return ClassifierPipeline(criteria)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            // fromPath(
            //     Path ("/home/londogard/git/londogard-nlp-toolkit/scripts/convert_huggingface/traced_Alireza1044/albert-base-v2-sst2.pt"),
            //     mapOf(),
            //     HuggingFaceTokenizerWrapper("Alireza1044/albert-base-v2-sst2"),
            //     engine = Engine.PYTORCH
            // ).use { pipeline -> pipeline.predict("Im sandy, I love this market! Its awesome").also(::println) }
        }
    }
}
