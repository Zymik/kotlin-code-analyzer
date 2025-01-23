package ru.kosolapov.analyzer.util

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

object TreeCopier {
    fun KotlinParseTree.deepCopy(): KotlinParseTree {
        return KotlinParseTree(type, name, text, children.map { it.deepCopy() }.toMutableList())
    }

    fun KotlinParseTree.copyWithChildren(children: List<KotlinParseTree>): KotlinParseTree {
        return KotlinParseTree(type, name, text, children.toMutableList())
    }
}
