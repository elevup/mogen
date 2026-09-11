package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass

/**
 * Parses serialised aliases from Kotlinx.serialization annotations
 */
class KotlinxSerializationAnnotationProcessor : AnnotationProcessor {

    override fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation> {
        // Serialised name of class members
        val fieldName = annotations.mapNotNull { annotation ->
            when (annotation) {
                is SerialName -> ProcessedAnnotation.FieldName(annotation.value)
                else -> null
            }
        }

        return fieldName + listOfNotNull(getEnumFieldNames(klass))
    }

    /**
     * Handles:
     * ```
     * @Serializable
     * enum class Foo {
     *  @SerialName("x")
     *  X,
     *  @SerialName("y")
     *  Y,
     *  @SerialName("z")
     *  Z;
     * }
     * ```
     */
    private fun getEnumFieldNames(klass: KClass<*>): ProcessedAnnotation? {
        if (!klass.java.isEnum) {
            return null
        }

        return klass.java.enumConstants.mapNotNull { enumConstant ->
            val name = (enumConstant as Enum<*>).name
            val annotation = klass.java.getField(name).getAnnotation(SerialName::class.java)
            if (annotation != null) {
                enumConstant as Any to annotation.value
            } else null
        }.toMap().takeIf { it.isNotEmpty() }?.let { ProcessedAnnotation.EnumNames(it) }
    }
}
