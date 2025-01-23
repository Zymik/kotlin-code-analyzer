package ru.kosolapov.analyzer.util

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.search.NodeSearcher.searchChildren

object ClassNameExtractor {
    fun getClassName(tree: KotlinParseTree): String {
        return tree
            .searchChildren(ParseTreeConstants.SIMPLE_IDENTIFIER)
            ?.searchChildren(ParseTreeConstants.IDENTIFIER)?.text!!
    }
}
