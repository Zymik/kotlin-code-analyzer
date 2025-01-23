package ru.kosolapov.analyzer.transform

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Transformer
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.search.Pattern
import ru.kosolapov.analyzer.search.Pattern.Companion.headMatch
import ru.kosolapov.analyzer.search.PatternDeleter
import ru.kosolapov.analyzer.util.ClassNameExtractor

object RemoveInnerClassesWithSameNameTransformer : Transformer {
    override fun transform(tree: KotlinParseTree) {
        require(tree.name == ParseTreeConstants.CLASS_DECLARATION)
        val pattern = classDeclarationPatten(ClassNameExtractor.getClassName(tree))
        PatternDeleter.deleteMatchingSubTries(tree, pattern)
    }

    private fun classDeclarationPatten(name: String): Pattern {
        return Pattern.SpecifiedChildrenMatch.allChildrenInList(
            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_MEMBER_DECLARATION),
            listOf(
                Pattern.SpecifiedChildrenMatch.allChildrenInList(
                    Pattern.NodeMetaChecker(name = ParseTreeConstants.DECLARATION),
                    listOf(
                        Pattern.AnyChildrenMatch(
                            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_DECLARATION),
                            Pattern.SpecifiedChildrenMatch.allChildrenInList(
                                Pattern.NodeMetaChecker(name = ParseTreeConstants.SIMPLE_IDENTIFIER),
                                listOf(
                                    headMatch(
                                        Pattern.NodeMetaChecker(
                                            name = ParseTreeConstants.IDENTIFIER,
                                            text = name,
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
