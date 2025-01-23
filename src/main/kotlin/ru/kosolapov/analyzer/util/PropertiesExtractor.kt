package ru.kosolapov.analyzer.util

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import ru.kosolapov.analyzer.parse.Modifier
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.parse.PropertyDeclaration
import ru.kosolapov.analyzer.search.NodeSearcher.searchAllChildren
import ru.kosolapov.analyzer.search.NodeSearcher.searchChildren
import ru.kosolapov.analyzer.search.Pattern
import ru.kosolapov.analyzer.search.PatternSearcher

object PropertiesExtractor {
    fun extractProperties(tree: KotlinParseTree): List<PropertyDeclaration> {
        require(tree.name == ParseTreeConstants.CLASS_DECLARATION) {
            "Expected class declaration"
        }

        return getConstructorProperties(tree) + getClassBodyProperties(tree)
    }

    private fun getConstructorProperties(tree: KotlinParseTree): List<PropertyDeclaration> {
        return tree.searchChildren(ParseTreeConstants.PRIMARY_CONSTRUCTOR)
            ?.searchChildren(ParseTreeConstants.CLASS_PARAMETERS)
            ?.searchAllChildren(ParseTreeConstants.CLASS_PARAMETER)
            ?.filter { it.children.any { child -> child.name == ParseTreeConstants.VAL || child.name == ParseTreeConstants.VAR } }
            ?.map {
                val identifier = searchIdentifier(it)
                PropertyDeclaration(identifier.text!!, extractModifiers(it), extractAnnotations(it))
            } ?: listOf()
    }

    private fun getClassBodyProperties(tree: KotlinParseTree): List<PropertyDeclaration> {
        val rawProperties =
            tree
                .searchChildren(ParseTreeConstants.CLASS_BODY)
                ?.searchChildren(ParseTreeConstants.CLASS_MEMBER_DECLARATIONS)
                ?.searchAllChildren(ParseTreeConstants.CLASS_MEMBER_DECLARATION)
                ?.mapNotNull { it.searchChildren(ParseTreeConstants.DECLARATION) }
                ?.mapNotNull { it.searchChildren(ParseTreeConstants.PROPERTY_DECLARATION) }
                ?: listOf()

        val properties =
            rawProperties.map { property ->
                val modifiers = extractModifiers(property)
                val name = searchIdentifier(property.searchChildren(ParseTreeConstants.VARIABLE_DECLARATION)!!)
                PropertyDeclaration(name.text!!, modifiers, extractAnnotations(property))
            }
        return properties
    }

    private fun extractModifiers(property: KotlinParseTree) =
        property.searchChildren(ParseTreeConstants.MODIFIERS)
            ?.searchAllChildren(ParseTreeConstants.MODIFIER)
            ?.mapNotNull { Modifier.parse(it.children[0].children[0].name) }
            ?.toSet() ?: setOf()

    private fun searchIdentifier(it: KotlinParseTree) =
        it
            .searchChildren(ParseTreeConstants.SIMPLE_IDENTIFIER)
            ?.searchChildren(ParseTreeConstants.IDENTIFIER)!!

    private fun extractAnnotations(property: KotlinParseTree): Set<String> {
        return property.searchChildren(ParseTreeConstants.MODIFIERS)
            ?.searchAllChildren(ParseTreeConstants.ANNOTATION)
            ?.mapNotNull { PatternSearcher.find(it, Pattern.headMatch(Pattern.NodeMetaChecker(name = ParseTreeConstants.IDENTIFIER))) }
            ?.mapNotNull { it.text }
            ?.toSet()
            ?: setOf()
    }
}
