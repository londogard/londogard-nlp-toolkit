package com.londogard.nlp.structures.trie

data class TrieNode<K, V>(val value: V?, val children: Map<K, TrieNode<K, V>>) {
    fun get(key: List<K>): V? = if (key.isEmpty()) value else children[key.first()]?.get(key.subList(1, key.size))

    companion object {
        fun <K, V> ofData(initialData: List<Pair<List<K>, V>>): TrieNode<K, V> {
            val (head, others) = initialData.partition { subSequence -> subSequence.first.isEmpty() }
            val value = head.firstOrNull()?.second

            val children = others
                .groupBy({ it.first.first() }, { it.first.subList(1, it.first.size) to it.second })
                .filterValues { it.isNotEmpty() }
                .mapValues { (_, d) -> ofData(d) }

            return TrieNode(value, children)
        }
    }
}