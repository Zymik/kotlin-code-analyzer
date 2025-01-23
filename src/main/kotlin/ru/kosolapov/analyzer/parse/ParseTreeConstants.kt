package ru.kosolapov.analyzer.parse

/**
 * Constants that use kotlin parse tree
 */
object ParseTreeConstants {
    const val CLASS_DECLARATION = "classDeclaration"
    const val CLASS_BODY = "classBody"
    const val CLASS_MEMBER_DECLARATIONS = "classMemberDeclarations"
    const val CLASS_MEMBER_DECLARATION = "classMemberDeclaration"
    const val DECLARATION = "declaration"
    const val COMPANION_OBJECT = "companionObject"
    const val OBJECT_DECLARATION = "objectDeclaration"
    const val PROPERTY_DECLARATION = "propertyDeclaration"
    const val MODIFIERS = "modifiers"
    const val MODIFIER = "modifier"
    const val ANNOTATION = "annotation"
    const val CLASS_MODIFIER = "classModifier"
    const val INNER = "INNER"
    const val VAL = "VAL"
    const val VAR = "VAR"
    const val PRIMARY_CONSTRUCTOR = "primaryConstructor"
    const val PRIMARY_EXPRESSION = "primaryExpression"
    const val POSTFIX_UNARY_EXPRESSION = "postfixUnaryExpression"
    const val THIS_EXPRESSION = "thisExpression"
    const val THIS = "THIS"
    const val THIS_AT = "THIS_AT"
    const val THIS_TEXT = "this"
    const val ASSIGNABLE_SUFFIX = "assignableSuffix"
    const val NAVIGATION_SUFFIX = "navigationSuffix"
    const val MEMBER_ACCESS_OPERATOR = "memberAccessOperator"
    const val DOT = "DOT"
    const val CLASS_PARAMETERS = "classParameters"
    const val CLASS_PARAMETER = "classParameter"
    const val SIMPLE_IDENTIFIER = "simpleIdentifier"
    const val IDENTIFIER = "Identifier"
    const val VARIABLE_DECLARATION = "variableDeclaration"
    const val DIRECTLY_ASSIGNABLE_EXPRESSION = "directlyAssignableExpression"
    const val WHEN_SUBJECT = "whenSubject"
    const val STATEMENT = "statement"
}
