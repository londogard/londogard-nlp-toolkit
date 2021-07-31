package com.londogard.nlp.structures.trie

import com.londogard.nlp.tokenizer.TokenizerSpecialTokens.BOW

class Trie(vocabulary: Map<String, Int>) {
    val rootNode: TrieNode

    init {
        val mutableRootNode = MutableTrieNode(BOW)

        vocabulary
            .forEach { (word, count) ->
                var currentNode = mutableRootNode
                mutableRootNode.count += count
                word
                    .forEach { char ->
                        currentNode = currentNode.childNodes.getOrPut(char) { MutableTrieNode(char) }
                        currentNode.count += count
                    }
                currentNode.isTerminal = true
            }
        rootNode = mutableRootNode.toTrieNode()
    }

    fun squashNode(path: List<Char>, replacement: Char): Trie {
        val truePath = if (path.firstOrNull() == BOW) path else (listOf(BOW) + path)
        if (truePath.size <= 2) {
            return this
        } else {
            val node = truePath
                .fold(rootNode) { currentNode, char -> currentNode.childNodes.getOrDefault(char, currentNode) }

            rootNode.childNodes.iterator()
        }
        TODO("")
    }

    // fun search(word: String)
}

// suffixSubword
// prefixSubword
// midSubword

// Add support for solo tokens (BOW, ALL_CAPS, UPPER, NUMBER, etc)
// Never squash unigrams
// Squash if 10x less, might be a good one?
// Squash if X

// Trie should be a path to walk, we then replace token with END trie character!

fun findFirstMerger(trie: TrieNode, string: String): String? {
    return when {
        trie.count > (10 * (trie.childNodes.maxOfOrNull { it.value.count } ?: 0)) -> {
            string + trie.char
        }
        trie.isTerminal -> null
        else -> {
            trie.childNodes.entries
                .mapNotNull { (_, value) -> findFirstMerger(value, string + trie.char) }
                .firstOrNull()
        }
    }
}