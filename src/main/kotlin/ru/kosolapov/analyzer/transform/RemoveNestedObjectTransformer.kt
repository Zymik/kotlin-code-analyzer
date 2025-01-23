package ru.kosolapov.analyzer.transform

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Transformer
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.search.Pattern
import ru.kosolapov.analyzer.search.Pattern.Companion.headMatch
import ru.kosolapov.analyzer.search.PatternDeleter

object RemoveNestedObjectTransformer : Transformer {
    private val objectDeclarationPattern =
        Pattern.SpecifiedChildrenMatch.allChildrenInList(
            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_MEMBER_DECLARATION),
            listOf(
                Pattern.SpecifiedChildrenMatch.allChildrenInList(
                    Pattern.NodeMetaChecker(name = ParseTreeConstants.DECLARATION),
                    listOf(
                        headMatch(Pattern.NodeMetaChecker(name = ParseTreeConstants.OBJECT_DECLARATION)),
                    ),
                ),
            ),
        )

    private val companionObjectDeclarationPattern =
        Pattern.SpecifiedChildrenMatch.allChildrenInList(
            Pattern.NodeMetaChecker(name = ParseTreeConstants.CLASS_MEMBER_DECLARATION),
            listOf(
                headMatch(Pattern.NodeMetaChecker(name = ParseTreeConstants.COMPANION_OBJECT)),
            ),
        )

    override fun transform(tree: KotlinParseTree) {
        PatternDeleter.deleteMatchingSubTries(tree, objectDeclarationPattern)
        PatternDeleter.deleteMatchingSubTries(tree, companionObjectDeclarationPattern)
    }
}
