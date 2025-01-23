package ru.kosolapov.analyzer.search

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

object PatternDeleter {
    fun deleteMatchingSubTries(
        tree: KotlinParseTree,
        pattern: Pattern,
    ) {
        val children =
            tree.children
                .filter {
                    !pattern.match(it)
                }
        children.forEach {
            deleteMatchingSubTries(it, pattern)
        }
        if (children.size != tree.children.size) {
            for (i in children.indices) {
                tree.children[i] = children[i]
            }
            for (i in children.size until tree.children.size) {
                tree.children.removeLast()
            }
        }
    }
}
