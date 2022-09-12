package com.londogard.nlp.meachinelearning.predictors.transformers

import ai.djl.Device
import ai.djl.repository.zoo.Criteria
import com.londogard.nlp.meachinelearning.predictors.transformers.translators.TokenClassificationTranslator
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper
import com.londogard.nlp.tokenizer.Tokenizer
import com.londogard.nlp.utils.huggingface.Engine
import com.londogard.nlp.utils.huggingface.HuggingFaceModelHub
import java.nio.file.Path

class TokenClassificationPipeline(model: Criteria<String, List<String>>): TransformerPipeline<String, List<String>>(model) {
    companion object {
        fun fromPath(
            modelPath: Path,
            id2label: Map<Int, String>,
            tokenizer: Tokenizer,
            engine: Engine,
            device: Device = Device.cpu()
        ): TokenClassificationPipeline {
            assert (tokenizer is HuggingFaceTokenizerWrapper) {
                "Current implementation requires HuggingFace tokenizers to be used"
            }
            val translator = TokenClassificationTranslator(tokenizer as HuggingFaceTokenizerWrapper, id2label)
            val criteria = baseCriteria(translator, modelPath, engine, device)

            return TokenClassificationPipeline(criteria)
        }
        fun create(
            modelName: String,
            engine: Engine = Engine.ONNX,
            device: Device = Device.cpu(),
        ): TokenClassificationPipeline {
            val model = HuggingFaceModelHub.load(modelName, engine)
            val configPath = model.localPath.parent.resolve("config.json")
            val config = HuggingFaceModelHub.id2label(configPath)
            val translator = TokenClassificationTranslator(HuggingFaceTokenizerWrapper(modelName), config)

            val criteria = baseCriteria(translator, model.localPath, engine, device)

            return TokenClassificationPipeline(criteria)
        }
    }
}