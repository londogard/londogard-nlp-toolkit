package com.londogard.nlp.meachinelearning.predictors.transformers

import ai.djl.Device
import ai.djl.inference.Predictor
import ai.djl.repository.zoo.Criteria
import ai.djl.repository.zoo.ZooModel
import ai.djl.training.util.ProgressBar
import ai.djl.translate.Translator
import com.londogard.nlp.utils.Engine
import java.nio.file.Path

abstract class  TransformerPipeline<I, O>(model: Criteria<I, O>) : AutoCloseable {
    private val loadedModel: ZooModel<I, O> = model.loadModel()
    private val predictor: Predictor<I, O> = loadedModel.newPredictor()

    fun predict(input: I): O = predictor.predict(input)

    fun predictBatch(inputs: List<I>): List<O> = predictor.batchPredict(inputs)

    override fun close() {
        loadedModel.close()
        predictor.close()
    }

    companion object {
        inline fun  <reified I, reified O> baseCriteria(
            translator: Translator<I, O>,
            modelPath: Path,
            engine: Engine,
            device: Device
        ): Criteria<I, O> =  Criteria.builder()
        .optEngine(engine.djlEngineName())
        .optDevice(device)
        .optProgress(ProgressBar())
        .setTypes(I::class.java, O::class.java)
        .optModelPath(modelPath)
        .optTranslator(translator)
        .build()
    }
}