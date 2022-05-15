package com.londogard.nlp.meachinelearning.predictors.classifiers

import ai.djl.Application
import ai.djl.Device
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer
import ai.djl.ndarray.NDList
import ai.djl.ndarray.NDManager
import ai.djl.repository.zoo.Criteria
import ai.djl.training.util.ProgressBar
import com.londogard.nlp.tokenizer.HuggingFaceTokenizerWrapper
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Simplest way to build a BERT model is to use ONNX and HuggingFace's conversion tool:
 * `python -m transformers.onnx --model=<model-name> <output-folder>`
 */
class BertClassifier(path: Path) {
    val criteria: Criteria<NDList, NDList> = Criteria.builder()
        .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
        .optEngine("OnnxRuntime")
        .setTypes(
            NDList::class.java,
            NDList::class.java
        ) // This model was traced on CPU and can only run on CPU
        .optDevice(Device.cpu())
        .optModelPath(path)
        .optProgress(ProgressBar())
        .build()

    fun predict(X: List<NDList>): List<NDList> {
        criteria.loadModel().use { model ->
            model.newPredictor().use { predictor ->
                return predictor.batchPredict(X)
            }
        }
    }
}
