package me.weiwen.tsundoku.datastructures

data class Trie<S, T>(var value: T?) {
    var children: MutableMap<S, Trie<S, T>> = mutableMapOf()

    operator fun set(key: S, child: Trie<S, T>) {
        children[key] = child
    }

    operator fun get(key: S): Trie<S, T>? {
        return children[key]
    }

    fun toList(): List<T> {
        if (value != null) {
            return listOf(value!!)
        } else {
            return children.values.flatMap { it.toList() }
        }
    }
}

fun Trie<Char, String>.add(word: String) {
    var node = this
    for (char in word) {
        if (node[char] == null) {
            node[char] = Trie(null)
        }
        node = node[char]!!
    }
    node.value = word
}