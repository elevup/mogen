package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

/**
 * Parses field constraints from JavaX annotations
 */
class JavaXAnnotationProcessor : AnnotationProcessor {

    override fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation> =
        annotations.map { annotation ->
            when (annotation) {
                is Size -> listOfNotNull(
                    ProcessedAnnotation.Minimum(annotation.min.toLong()),
                    ProcessedAnnotation.Maximum(annotation.max.toLong()).takeIf { annotation.max != Int.MAX_VALUE },
                )
                is Min -> listOf(
                    ProcessedAnnotation.Minimum(annotation.value)
                )
                is Max -> listOf(
                    ProcessedAnnotation.Maximum(annotation.value)
                )
                is Pattern -> listOf(
                    ProcessedAnnotation.Regex(annotation.regexp)
                )
                else -> emptyList()
            }
        }.flatten()

}
