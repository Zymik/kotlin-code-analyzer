package ru.kosolapov.analyzer.analyze.rule

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Error
import ru.kosolapov.analyzer.analyze.Transformer
import ru.kosolapov.analyzer.analyze.check.Check
import ru.kosolapov.analyzer.util.TreeCopier.deepCopy

abstract class Rule(
    private val onNodes: Set<String>,
    private val deepCheck: Boolean = false,
    private val check: Check,
    private val transformer: Transformer,
    val name: String,
) {
    fun apply(tree: KotlinParseTree): List<Error> {
        return buildList {
            if (tree.name in onNodes) {
                val copy = tree.deepCopy()
                transformer.transform(copy)
                addAll(check.check(copy))
                if (!deepCheck) return@buildList
            }
            if (tree.name !in onNodes) {
                tree.children.forEach {
                    addAll(apply(it))
                }
            }
        }
    }
}
