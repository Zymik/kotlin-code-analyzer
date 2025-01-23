package ru.kosolapov.analyzer.analyze

import org.apache.logging.log4j.LogManager
import org.jetbrains.kotlin.spec.grammar.tools.KotlinParseTree
import org.jetbrains.kotlin.spec.grammar.tools.parseKotlinCode
import org.jetbrains.kotlin.spec.grammar.tools.tokenizeKotlinCode
import ru.kosolapov.analyzer.analyze.rule.Rule
import java.nio.file.Files
import java.nio.file.Path

class Analyzer(
    private val rules: List<Rule>,
) {
    companion object {
        private val log = LogManager.getLogger(Analyzer::class.java)
    }

    fun analyze(tree: KotlinParseTree): List<Error> {
        return rules.flatMap {
            log.info("Applying rule ${it.name}")
            it.apply(tree)
        }
    }

    fun analyze(code: String): List<Error> {
        return analyze(parseKotlinCode(tokenizeKotlinCode(code)))
    }

    fun analyzeFile(file: Path): List<Error> {
        return analyze(Files.readString(file)).map { errorInFile(file, it) }
    }

    fun analyzeFolder(folder: Path): List<Error> {
        log.info("Analyzing folder $folder")
        val files =
            Files.walk(folder).use {
                it.filter { file ->
                    Files.isRegularFile(file) && file.toString().lowercase().endsWith(".kt")
                }.toList()
            }
        return files.flatMap {
            log.info("Analyzing $it")
            analyzeFile(it).map { error -> errorInFile(it, error) }
        }
    }

    private fun errorInFile(
        path: Path,
        error: Error,
    ) = Error("Error in file $path: ${error.message}")
}
