package ru.kosolapov.analyzer.search

import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTreeNodeType

fun interface Pattern {
    data class NodeMetaChecker(
        val name: String? = null,
        val text: String? = null,
        val type: KotlinParseTreeNodeType? = null,
    ) {
        fun match(tree: KotlinParseTree): Boolean {
            return (name == null || tree.name == name) && (text == null || tree.text == text) && (type == null || tree.type == type)
        }
    }

    fun match(tree: KotlinParseTree): Boolean

    data object WildCard : Pattern {
        override fun match(tree: KotlinParseTree): Boolean = true
    }

    data class SpecifiedChildrenMatch(
        val head: NodeMetaChecker,
        val children: Map<Int, Pattern> = mapOf(),
        val childrenCount: Int? = null,
    ) : Pattern {
        companion object {
            fun allChildrenInList(
                head: NodeMetaChecker,
                children: List<Pattern>,
            ): SpecifiedChildrenMatch {
                children.mapIndexed { i, v -> i to v }.toMap()
                return SpecifiedChildrenMatch(head, children.mapIndexed { i, v -> i to v }.toMap(), children.size)
            }

            fun oneChildren(
                head: NodeMetaChecker,
                pattern: Pattern,
            ): SpecifiedChildrenMatch = allChildrenInList(head, listOf(pattern))
        }

        override fun match(tree: KotlinParseTree): Boolean {
            if (!head.match(tree)) return false
            if (childrenCount != null && childrenCount != tree.children.size) return false
            children.forEach { (k, v) ->
                if (!v.match(tree.children[k])) return false
            }
            return true
        }
    }

    data class AllChildrenMatch(
        val head: NodeMetaChecker,
        val children: Pattern,
    ) : Pattern {
        override fun match(tree: KotlinParseTree): Boolean {
            return head.match(tree) && tree.children.all { children.match(it) }
        }
    }

    data class AnyChildrenMatch(val head: NodeMetaChecker, val children: Pattern) : Pattern {
        override fun match(tree: KotlinParseTree): Boolean {
            if (!head.match(tree)) return false
            return tree.children.any {
                children.match(it)
            }
        }
    }

    fun negate() =
        Pattern {
            !match(it)
        }

    companion object {
        fun headMatch(head: NodeMetaChecker) = SpecifiedChildrenMatch(head, mapOf(), null)

        fun existPath(path: List<NodeMetaChecker>): Pattern {
            require(path.isNotEmpty())
            return existPath(path.asSequence())
        }

        private fun existPath(seq: Sequence<NodeMetaChecker>): Pattern {
            val head = seq.firstOrNull()
            require(head != null)
            val tail = seq.drop(1)

            return if (tail.firstOrNull() == null) {
                headMatch(head)
            } else {
                AnyChildrenMatch(head, existPath(tail))
            }
        }
    }
}
