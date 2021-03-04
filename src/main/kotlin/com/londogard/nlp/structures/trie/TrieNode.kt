package com.londogard.nlp.structures.trie

data class TrieNode(val char: Char, val isTerminal: Boolean, val count: Int, val childNodes: Map<Char, TrieNode>) {
    fun toMutableTrieNode(): MutableTrieNode =
        MutableTrieNode(
            char,
            isTerminal,
            count,
            childNodes.map { (key, value) -> key to value.toMutableTrieNode() }.toMap(HashMap(childNodes.size))
        )
}

data class MutableTrieNode(val char: Char, var isTerminal: Boolean = false, var count: Int = 0, val childNodes: MutableMap<Char, MutableTrieNode> = mutableMapOf()) {
    fun toTrieNode(): TrieNode =
        TrieNode(char, isTerminal, count, childNodes.map { (key, value) -> key to value.toTrieNode() }.toMap())
}
