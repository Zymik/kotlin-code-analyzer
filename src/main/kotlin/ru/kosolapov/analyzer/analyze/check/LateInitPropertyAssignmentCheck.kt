package ru.kosolapov.analyzer.analyze.check

import org.apache.logging.log4j.LogManager
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.analyze.Error
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.parse.PropertyDeclaration
import ru.kosolapov.analyzer.search.Pattern
import ru.kosolapov.analyzer.util.ClassNameExtractor
import ru.kosolapov.analyzer.util.PropertiesExtractor

class LateInitPropertyAssignmentCheck(
    private val ignoreAnnotations: Set<String>,
) : Check {
    companion object {
        private val log = LogManager.getLogger(LateInitPropertyAssignmentCheck::class.java)
    }

    override fun check(tree: KotlinParseTree): List<Error> {
        check(tree.name == ParseTreeConstants.CLASS_DECLARATION) {
            "Can check only class declarations"
        }

        val className = ClassNameExtractor.getClassName(tree)

        log.info("Checking $className late init private properties")

        val invalidProperties =
            PropertiesExtractor
                .extractProperties(tree)
                .filter { isInvalidProperty(it, tree) }

        return invalidProperties.map { Error("Late init property '${it.name}' of class $className have no assignment") }
    }

    private fun isInvalidProperty(
        property: PropertyDeclaration,
        tree: KotlinParseTree,
    ) = property.isLateInit() && property.isPrivate() &&
        property.annotations.intersect(ignoreAnnotations)
            .isEmpty() && !checkPropertyAssignment(tree, property.name)

    private fun checkPropertyAssignment(
        tree: KotlinParseTree,
        propertyName: String,
    ): Boolean {
        return checkSimpleDeclaration(
            tree,
            propertyName,
        ) || checkThisDeclaration(tree, propertyName) || checkFullThisDeclaration(tree, propertyName)
    }

    private fun checkSimpleDeclaration(
        tree: KotlinParseTree,
        propertyName: String,
    ): Boolean {
        val variableDeclarationPattern = assignmentVariableDeclarationPattern(propertyName)
        val whenDeclarationPattern = assignmentWhenVariableDeclaration(propertyName)
        return tree.children.any { subTree ->
            checkPropertyAssignment(subTree, assignmentPropertyPattern(propertyName), {
                variableDeclarationPattern.match(it) || whenDeclarationPattern.match(it)
            }) {
                it.name == ParseTreeConstants.CLASS_DECLARATION && propertyName in
                    PropertiesExtractor.extractProperties(
                        it,
                    ).map { property -> property.name }
            }
        }
    }

    private fun checkThisDeclaration(
        tree: KotlinParseTree,
        propertyName: String,
    ): Boolean {
        val thisVariableDeclaration = thisAssignmentVariableDeclaration(propertyName)
        return tree.children.any { subTree ->
            checkPropertyAssignment(subTree, thisVariableDeclaration, { false }) {
                it.name == ParseTreeConstants.CLASS_DECLARATION
            }
        }
    }

    private fun checkFullThisDeclaration(
        tree: KotlinParseTree,
        propertyName: String,
    ): Boolean {
        val thisVariableDeclaration =
            thisAssignmentVariableDeclaration(propertyName, ClassNameExtractor.getClassName(tree))
        return tree.children.any { subTree ->
            checkPropertyAssignment(subTree, thisVariableDeclaration, { false }, { false })
        }
    }

    private fun checkPropertyAssignment(
        tree: KotlinParseTree,
        pattern: Pattern,
        drop: (KotlinParseTree) -> Boolean,
        skip: (KotlinParseTree) -> Boolean,
    ): Boolean {
        if (skip(tree)) return false
        return pattern.match(tree) ||
            tree.children.takeWhile { !drop(it) }
                .any { checkPropertyAssignment(it, pattern, drop, skip) }
    }

    private fun assignmentPropertyPattern(propertyName: String) =
        Pattern.SpecifiedChildrenMatch.oneChildren(
            Pattern.NodeMetaChecker(name = ParseTreeConstants.DIRECTLY_ASSIGNABLE_EXPRESSION),
            Pattern.SpecifiedChildrenMatch.oneChildren(
                Pattern.NodeMetaChecker(name = ParseTreeConstants.SIMPLE_IDENTIFIER),
                Pattern.headMatch(Pattern.NodeMetaChecker(name = ParseTreeConstants.IDENTIFIER, text = propertyName)),
            ),
        )

    private fun assignmentVariableDeclarationPattern(propertyName: String) =
        Pattern.existPath(
            listOf(
                Pattern.NodeMetaChecker(name = ParseTreeConstants.STATEMENT),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.DECLARATION),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.PROPERTY_DECLARATION),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.VARIABLE_DECLARATION),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.SIMPLE_IDENTIFIER),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.IDENTIFIER, text = propertyName),
            ),
        )

    private fun assignmentWhenVariableDeclaration(propertyName: String) =
        Pattern.existPath(
            listOf(
                Pattern.NodeMetaChecker(name = ParseTreeConstants.WHEN_SUBJECT),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.VARIABLE_DECLARATION),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.SIMPLE_IDENTIFIER),
                Pattern.NodeMetaChecker(name = ParseTreeConstants.IDENTIFIER, text = propertyName),
            ),
        )

    private fun thisAssignmentVariableDeclaration(
        propertyName: String,
        className: String? = null,
    ) = Pattern.SpecifiedChildrenMatch.allChildrenInList(
        Pattern.NodeMetaChecker(name = ParseTreeConstants.DIRECTLY_ASSIGNABLE_EXPRESSION),
        listOf(
            Pattern.SpecifiedChildrenMatch.oneChildren(
                Pattern.NodeMetaChecker(name = ParseTreeConstants.POSTFIX_UNARY_EXPRESSION),
                Pattern.SpecifiedChildrenMatch.oneChildren(
                    Pattern.NodeMetaChecker(name = ParseTreeConstants.PRIMARY_EXPRESSION),
                    Pattern.SpecifiedChildrenMatch.oneChildren(
                        Pattern.NodeMetaChecker(name = ParseTreeConstants.THIS_EXPRESSION),
                        Pattern.headMatch(
                            Pattern.NodeMetaChecker(
                                name =
                                    if (className != null) {
                                        ParseTreeConstants.THIS_AT
                                    } else {
                                        ParseTreeConstants.THIS
                                    },
                                text =
                                    if (className != null) {
                                        ParseTreeConstants.THIS_TEXT + "@$className"
                                    } else {
                                        ParseTreeConstants.THIS_TEXT
                                    },
                            ),
                        ),
                    ),
                ),
            ),
            Pattern.SpecifiedChildrenMatch.oneChildren(
                Pattern.NodeMetaChecker(name = ParseTreeConstants.ASSIGNABLE_SUFFIX),
                Pattern.SpecifiedChildrenMatch.allChildrenInList(
                    Pattern.NodeMetaChecker(name = ParseTreeConstants.NAVIGATION_SUFFIX),
                    listOf(
                        Pattern.SpecifiedChildrenMatch.oneChildren(
                            Pattern.NodeMetaChecker(name = ParseTreeConstants.MEMBER_ACCESS_OPERATOR),
                            Pattern.headMatch(Pattern.NodeMetaChecker(name = ParseTreeConstants.DOT)),
                        ),
                        Pattern.SpecifiedChildrenMatch.oneChildren(
                            Pattern.NodeMetaChecker(name = ParseTreeConstants.SIMPLE_IDENTIFIER),
                            Pattern.headMatch(
                                Pattern.NodeMetaChecker(
                                    name = ParseTreeConstants.IDENTIFIER,
                                    text = propertyName,
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )
}
