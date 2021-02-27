package com.londogard.nlp.structures.trie

data class TrieNode(val char: Char?, val count: Int, val childNodes: Map<Char, TrieNode>)
data class MutableTrieNode(val char: Char?, var count: Int = 0, val childNodes: MutableMap<Char, MutableTrieNode> = mutableMapOf()) {
    fun toTrieNode(): TrieNode =
        TrieNode(char, count, childNodes.map { (key, value) -> key to value.toTrieNode() }.toMap())
}
