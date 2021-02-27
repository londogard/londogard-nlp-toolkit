package com.londogard.nlp.structures.trie

class Trie(vocabulary: Sequence<String>) {
    private val rootNode: TrieNode

    init {
        val mutableRootNode = MutableTrieNode(null, 0, mutableMapOf())
        vocabulary.forEach { word ->
            var currentNode = mutableRootNode
            word.forEach { char ->
                currentNode = currentNode.childNodes.getOrPut(char) { MutableTrieNode(char) }
            }
            currentNode.count += 1
        }
        rootNode = mutableRootNode.toTrieNode()
    }

    // fun search(word: String)
}