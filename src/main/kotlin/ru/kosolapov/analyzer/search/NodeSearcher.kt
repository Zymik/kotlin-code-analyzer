package ru.kosolapov.analyzer.search

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

object NodeSearcher {
    fun KotlinParseTree.searchChildren(type: String): KotlinParseTree? {
        children.forEach {
            if (it.name == type) {
                return it
            }
        }
        return null
    }

    fun KotlinParseTree.searchAllChildren(type: String): List<KotlinParseTree> {
        return children.filter { it.name == type }
    }
}
