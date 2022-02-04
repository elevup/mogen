package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import kotlin.reflect.KClass

/**
 * Checks if given field is deprecated and grabs reason why
 */
class DeprecatedAnnotationProcessor : AnnotationProcessor {

    override fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation> =
        annotations.mapNotNull { annotation ->
            when (annotation) {
                is Deprecated -> ProcessedAnnotation.Deprecated(annotation.message)
                else -> null
            }
        }
}
