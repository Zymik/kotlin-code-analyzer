package ru.kosolapov.analyzer.parse

data class PropertyDeclaration(
    val name: String,
    val modifiers: Set<Modifier>,
    val annotations: Set<String> = setOf(),
) {
    fun isPrivate() = Modifier.PRIVATE in modifiers

    fun isLateInit() = Modifier.LATEINIT in modifiers
}

enum class Modifier {
    PRIVATE,
    PROTECTED,
    PUBLIC,
    LATEINIT,
    INTERNAL,
    ;

    companion object {
        fun parse(value: String) = entries.firstOrNull { it.name == value }
    }
}
