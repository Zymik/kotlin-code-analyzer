package ru.kosolapov.analyzer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.path
import ru.kosolapov.analyzer.analyze.Analyzer
import ru.kosolapov.analyzer.analyze.rule.LateInitPropertyAssignmentRule
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

class AnalyzerCli : CliktCommand() {
    private val path: Path by option().path().required().check("Must be file or directory") {
        Files.isDirectory(it) || Files.isRegularFile(it)
    }
    private val useLateInitCheck: Boolean by option().flag(default = true)
    private val lateInitAnnotations: Set<String> by option().multiple().unique()

    override fun run() {
        if (!useLateInitCheck) {
            echo("All checks passed")
            return
        }

        val analyzer =
            Analyzer(
                listOf(
                    LateInitPropertyAssignmentRule(
                        lateInitAnnotations,
                    ),
                ),
            )
        val errors =
            if (Files.isRegularFile(path)) {
                analyzer.analyzeFile(path)
            } else {
                analyzer.analyzeFolder(path)
            }
        if (errors.isEmpty()) {
            echo("All checks passed")
            return
        }
        errors.forEach {
            echo(it.message, err = true)
        }
        exitProcess(1)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = AnalyzerCli().main(args)
    }
}
