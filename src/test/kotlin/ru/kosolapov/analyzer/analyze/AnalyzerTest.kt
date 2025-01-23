package ru.kosolapov.analyzer.analyze

import org.assertj.core.api.Assertions.assertThat
import ru.kosolapov.analyzer.analyze.rule.LateInitPropertyAssignmentRule
import java.nio.file.Path
import kotlin.test.Test

class AnalyzerTest {
    private val analyzer =
        Analyzer(
            listOf(
                LateInitPropertyAssignmentRule(
                    setOf("Autowired"),
                ),
            ),
        )

    @Test
    fun `has assignment`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitAssignment.txt"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `has annotation`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitAnnotation.txt"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `has assignment by this`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitThisAssignment.txt"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `has full this assignment`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitFullThisAssignment.txt"))
        assertThat(result).isEmpty()
    }

    @Test
    fun `no assignment`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitNoAssignment.txt"))
        assertThat(result).hasSize(1)
    }

    @Test
    fun `assignment of local val`() {
        val result = analyzer.analyzeFile(getResource("lateinit/LateInitLocalValAssignment.txt"))
        assertThat(result).hasSize(1)
    }

    @Test
    fun `assignment of local val in nested block`() {
        val result =
            analyzer.analyzeFile(
                getResource("lateinit/LateInitAssignmentOfLocalValInNestedBlock.txt"),
            )
        assertThat(result).hasSize(0)
    }

    @Test
    fun `assignment of inner class property`() {
        val result =
            analyzer.analyzeFile(
                getResource("lateinit/LateInitAssignmentOfInnerClassProperty.txt"),
            )
        assertThat(result).hasSize(1)
    }

    @Test
    fun `assignment of nested class property`() {
        val result =
            analyzer.analyzeFile(
                getResource("lateinit/LateInitAssignmentOfNestedClassProperty.txt"),
            )
        assertThat(result).hasSize(1)
    }

    @Test
    fun `assignment of nested object property`() {
        val result =
            analyzer.analyzeFile(
                getResource("lateinit/LateInitAssignmentOfNestedObjectProperty.txt"),
            )
        assertThat(result).hasSize(1)
    }

    @Test
    fun `assignment of companion object property`() {
        val result =
            analyzer.analyzeFile(
                getResource("lateinit/LateInitAssignmentOfCompanionObjectProperty.txt"),
            )
        assertThat(result).hasSize(1)
    }

    private fun getResource(resource: String): Path {
        val r =
            javaClass.classLoader.getResource(resource) ?: throw IllegalArgumentException("No resource at $resource")
        return Path.of(r.toURI())
    }
}
