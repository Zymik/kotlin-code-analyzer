package ru.kosolapov.analyzer.analyze.check

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Error

interface Check {
    fun check(tree: KotlinParseTree): List<Error>
}
