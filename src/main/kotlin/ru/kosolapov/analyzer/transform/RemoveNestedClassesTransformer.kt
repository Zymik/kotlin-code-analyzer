package ru.kosolapov.analyzer.transform

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Transformer
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.search.Pattern
import ru.kosolapov.analyzer.search.PatternDeleter

object RemoveNestedClassesTransformer : Transformer {
    private val nestedClassesPattern =
        Pattern.SpecifiedChildrenMatch.allChildrenInList(
            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_MEMBER_DECLARATION),
            listOf(
                Pattern.SpecifiedChildrenMatch.allChildrenInList(
                    Pattern.NodeMetaChecker(name = ParseTreeConstants.DECLARATION),
                    listOf(
                        Pattern.AllChildrenMatch(
                            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_DECLARATION),
                            Pattern.existPath(
                                listOf(
                                    Pattern.NodeMetaChecker(name = ParseTreeConstants.MODIFIERS),
                                    Pattern.NodeMetaChecker(name = ParseTreeConstants.MODIFIER),
                                    Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_MODIFIER),
                                    Pattern.NodeMetaChecker(name = ParseTreeConstants.INNER),
                                ),
                            ).negate(),
                        ),
                    ),
                ),
            ),
        )

    override fun transform(tree: KotlinParseTree) {
        require(tree.name == ParseTreeConstants.CLASS_DECLARATION)
        PatternDeleter.deleteMatchingSubTries(tree, nestedClassesPattern)
    }
}
