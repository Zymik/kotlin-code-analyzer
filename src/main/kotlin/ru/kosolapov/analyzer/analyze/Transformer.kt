package ru.kosolapov.analyzer.analyze

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree

/**
 * Transform tree to simplify analzation
 */
fun interface Transformer {
    fun transform(tree: KotlinParseTree)

    companion object {
        fun from(vararg transformers: Transformer): Transformer =
            Transformer { tree ->
                transformers.forEach {
                    it.transform(tree)
                }
            }
    }
}
