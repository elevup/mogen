package com.elevup.processors

import com.elevup.annotation.KotlinxSerializationAnnotationProcessor
import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.models.Types
import com.elevup.models.appendAndExpectOutput
import io.kotest.core.spec.style.StringSpec
import kotlinx.serialization.SerialName

data class KotlinxClass(
    @SerialName("colour")
    val color: KotlinxColor,
    val size: KotlinxSize,
)

enum class KotlinxColor {
    @SerialName("r")
    RED,

    @SerialName("g")
    GREEN
}

enum class KotlinxSize {
    SMALL,
    LARGE
}

class KotlinxSerializationTests : StringSpec({
    val processors = listOf(KotlinxSerializationAnnotationProcessor())

    "TypeScript Kotlinx.serialization integration" {
        TypeScriptGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = KotlinxClass::class,
            classes = Types(
                """
                export interface KotlinxClass {
                  colour: KotlinxColor;
                  size: KotlinxSize;
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                export enum KotlinxColor {
                  RED = 'r',
                  GREEN = 'g',
                }
                """.trimIndent(),
                """
                export enum KotlinxSize {
                  SMALL = 'SMALL',
                  LARGE = 'LARGE',
                }
                """.trimIndent(),
            )
        )
    }

    "Swift Kotlinx.serialization integration" {
        SwiftGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = KotlinxClass::class,
            classes = Types(
                """
                struct KotlinxClass: Codable {
                  let colour: KotlinxColor
                  let size: KotlinxSize
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                enum KotlinxColor: String, Codable {
                  case red = "r"
                  case green = "g"
                }
                """.trimIndent(),
                """
                enum KotlinxSize: String, Codable {
                  case small = "SMALL"
                  case large = "LARGE"
                }
                """.trimIndent(),
            )
        )
    }

    "Dart Kotlinx.serialization integration" {
        DartGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = KotlinxClass::class,
            classes = Types(
                """
                class KotlinxClass {
                  final KotlinxColor colour;
                  final KotlinxSize size;
                
                  KotlinxClass({
                    required this.colour,
                    required this.size,
                  });
                
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                enum KotlinxColor {
                  RED, // = r
                  GREEN, // = g
                }
                """.trimIndent(),
                """
                enum KotlinxSize {
                  SMALL, // = SMALL
                  LARGE, // = LARGE
                }
                """.trimIndent(),
            )
        )
    }
})
