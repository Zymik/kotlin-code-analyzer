package ru.kosolapov.analyzer.search

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

object PatternSearcher {
    fun find(
        tree: KotlinParseTree,
        pattern: Pattern,
    ): KotlinParseTree? {
        if (pattern.match(tree)) return tree

        return tree.children.firstNotNullOfOrNull { find(it, pattern) }
    }
}
