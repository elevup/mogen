package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import com.elevup.skrutiny.annotations.Length
import com.elevup.skrutiny.annotations.Max
import com.elevup.skrutiny.annotations.Min
import com.elevup.skrutiny.annotations.Pattern
import java.math.BigDecimal
import kotlin.reflect.KClass

/**
 * Parses constraints from Skrutiny annotations
 */
class SkrutinyAnnotationProcessor : AnnotationProcessor {

    override fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation> =
        annotations.flatMap { annotation ->
            when (annotation) {
                is Length -> listOfNotNull(
                    ProcessedAnnotation.Minimum(annotation.min.toLong()),
                    ProcessedAnnotation.Maximum(annotation.max.toLong()).takeIf { annotation.max != Int.MAX_VALUE },
                )

                is Min -> listOf(ProcessedAnnotation.Minimum(BigDecimal(annotation.value).toLong()))
                is Max -> listOf(ProcessedAnnotation.Maximum(BigDecimal(annotation.value).toLong()))
                is Pattern -> listOf(ProcessedAnnotation.Regex(annotation.regex))
                else -> emptyList()
            }
        }

}
