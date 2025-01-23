package ru.kosolapov.analyzer.analyze.rule

import ru.kosolapov.analyzer.analyze.Transformer
import ru.kosolapov.analyzer.analyze.check.LateInitPropertyAssignmentCheck
import ru.kosolapov.analyzer.parse.ParseTreeConstants
import ru.kosolapov.analyzer.transform.RemoveInnerClassesWithSameNameTransformer
import ru.kosolapov.analyzer.transform.RemoveNestedClassesTransformer
import ru.kosolapov.analyzer.transform.RemoveNestedObjectTransformer

class LateInitPropertyAssignmentRule(
    ignoreAnnotations: Set<String>,
) : Rule(
        onNodes = setOf(ParseTreeConstants.CLASS_DECLARATION),
        check = LateInitPropertyAssignmentCheck(ignoreAnnotations),
        transformer =
            Transformer.from(
                RemoveNestedObjectTransformer,
                RemoveInnerClassesWithSameNameTransformer,
                RemoveNestedClassesTransformer,
            ),
        name = "LateInitPropertyAssignmentRule",
    )
