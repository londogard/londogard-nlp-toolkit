package com.londogard.nlp.machinelearning

class TransformersTest {
    /**
     *
    object a {
    @JvmStatic
    fun main(args: Array<String>) {
    NDManager.newBaseManager().use { manager ->
    val tokenizer = HuggingFaceTokenizerWrapper(HuggingFaceTokenizer.newInstance("albert-base-v2"))
    val encodedInput = tokenizer.encode("random")
    val a = BertClassifier(Path(javaClass.getResource("/onnx/albert.onnx/model.onnx").path).toAbsolutePath())
    .criteria
    .loadModel()
    .newPredictor()
    .predict(NDList(
    manager.create(encodedInput.ids).expandDims(0),
    manager.create(encodedInput.attentionMask).expandDims(0),
    manager.create(encodedInput.typeIds).expandDims(0)
    ))
    println(a[0].squeeze().flatten()[0])
    println(a.shapes.toList())
    println(a[0].squeeze().flatten()[767])
    }
    }
    }
     */
}