package com.londogard.nlp.utils.huggingface

import java.nio.file.Path

/** Wraps the Path to the model */
@JvmInline value class HFModel(val localPath: Path)