package com.elevup.annotation

import com.elevup.annotation.model.ProcessedAnnotation
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import kotlin.reflect.KClass

/**
 * Parses serialised aliases from Jackson annotations
 */
class JacksonAnnotationProcessor : AnnotationProcessor {

    override fun process(annotations: Iterable<Annotation>, klass: KClass<*>): List<ProcessedAnnotation> {
        // Field name of enum method properly annotated
        val enumNamesMethod = getEnumFieldNamesFromMethod(klass)
        // Field name of enum constants attached to them
        val enumNamesField = getEnumFieldNamesFromField(klass)
        // Field name of class members
        val fieldName = annotations.mapNotNull { annotation ->
            when (annotation) {
                is JsonProperty -> ProcessedAnnotation.FieldName(annotation.value)
                else -> null
            }
        }

        return fieldName + listOfNotNull(enumNamesMethod, enumNamesField)
    }

    /**
     * Handles:
     * ```
     * enum class Foo (private val someField: String) {
     *  X("x"),
     *  Y("y"),
     *  Z("z");
     *
     *  @JsonValue
     *  fun stuff() = someField
     * }
     * ```
     */
    private fun getEnumFieldNamesFromMethod(klass: KClass<*>): ProcessedAnnotation? {
        if (!klass.java.isEnum) {
            return null
        }

        val eligibleMethods = klass.java.methods.mapNotNull {
            val annotation = it.annotations
                .filterIsInstance(JsonValue::class.java)
                .filter { it.value }

            it.takeIf { annotation.isNotEmpty() }
        }

        if (eligibleMethods.size > 1) {
            throw IllegalStateException("Attempting to figure out serialized name for $klass but multiple methods match $eligibleMethods")
        }

        return eligibleMethods.getOrNull(0)?.let { method ->
            klass.java.enumConstants.mapNotNull { enumConstant ->
                (method.invoke(enumConstant) as? String)?.let {
                    enumConstant to it
                }
            }.toMap()
        }?.let { ProcessedAnnotation.EnumNames(it) }
    }

    /**
     * Handles:
     * ```
     * enum class Foo (private val someField: String) {
     *  @JsonProperty("x")
     *  X,
     *  @JsonProperty("y")
     *  Y,
     *  @JsonProperty("z")
     *  Z;
     * }
     * ```
     */
    private fun getEnumFieldNamesFromField(klass: KClass<*>) : ProcessedAnnotation? {
        if (!klass.java.isEnum) {
            return null
        }

        return klass.java.enumConstants.mapNotNull { enumConstant ->
            val name = (enumConstant as Enum<*>).name
            val annotation = klass.java.getField(name).getAnnotation(JsonProperty::class.java)
            if (annotation != null) {
                enumConstant as Any to annotation.value
            } else null
        }.toMap().takeIf { it.isNotEmpty() }?.let { ProcessedAnnotation.EnumNames(it) }
    }
}
