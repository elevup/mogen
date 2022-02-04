package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import kotlin.reflect.KClass

interface AnnotationProcessor {

    /**
     * Takes list of [annotations] for given [klass] and generates our own annotations
     */
    fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation>
}
