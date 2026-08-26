package com.elevup.annotation.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AnnotationMergerTests : StringSpec({

    "duplicate equal candidates are deduplicated instead of failing" {
        // Kotlin's default `param-property` annotation-target scheme can copy one source
        // annotation onto both a constructor parameter and its backing field/property,
        // producing two equal ProcessedAnnotation instances for the same logical annotation.
        listOf(
            ProcessedAnnotation.FieldName("id"),
            ProcessedAnnotation.FieldName("id"),
        ).merge() shouldBe MergedAnnotations(fieldName = "id")

        listOf(
            ProcessedAnnotation.Minimum(1),
            ProcessedAnnotation.Minimum(1),
        ).merge() shouldBe MergedAnnotations(min = 1)

        listOf(
            ProcessedAnnotation.Maximum(10),
            ProcessedAnnotation.Maximum(10),
        ).merge() shouldBe MergedAnnotations(max = 10)

        listOf(
            ProcessedAnnotation.Regex("^[a-z]+$"),
            ProcessedAnnotation.Regex("^[a-z]+$"),
        ).merge() shouldBe MergedAnnotations(regex = "^[a-z]+$")
    }

    "genuinely conflicting candidates still throw" {
        shouldThrow<IllegalStateException> {
            listOf(
                ProcessedAnnotation.FieldName("id"),
                ProcessedAnnotation.FieldName("identifier"),
            ).merge()
        }

        shouldThrow<IllegalStateException> {
            listOf(
                ProcessedAnnotation.Minimum(1),
                ProcessedAnnotation.Minimum(2),
            ).merge()
        }
    }
})
