package com.elevup.processors

import com.elevup.annotation.JacksonAnnotationProcessor
import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.models.Types
import com.elevup.models.appendAndExpectOutput
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import io.kotest.core.spec.style.StringSpec

data class JacksonClass(
    @JsonProperty("colour")
    val color: Color,
    val shape: Shape,
)

enum class Color {
    @JsonProperty("r")
    RED,

    @JsonProperty("g")
    GREEN
}

enum class Shape(private val serializedValue: String) {
    RECTANGLE("rect"),
    CIRCLE("circ"),
    ;

    @JsonValue
    fun getApiValue() = serializedValue
}

class JacksonTests : StringSpec({
    val processors = listOf(JacksonAnnotationProcessor())

    "TypeScript Jackson integration" {
        TypeScriptGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = JacksonClass::class,
            classes = Types(
                """
                export interface JacksonClass {
                  colour: Color;
                  shape: Shape;
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                export enum Color {
                  RED = 'r',
                  GREEN = 'g',
                }
                """.trimIndent(),
                """
                export enum Shape {
                  RECTANGLE = 'rect',
                  CIRCLE = 'circ',
                }
                """.trimIndent(),
            )
        )
    }

    "Swift Jackson integration" {
        SwiftGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = JacksonClass::class,
            classes = Types(
                """
                struct JacksonClass: Codable {
                  let colour: Color
                  let shape: Shape
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                enum Color: String, Codable {
                  case red = "r"
                  case green = "g"
                }
                """.trimIndent(),
                """
                enum Shape: String, Codable {
                  case rectangle = "rect"
                  case circle = "circ"
                }
                """.trimIndent(),
            )
        )
    }

    "Dart Jackson integration" {
        DartGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = JacksonClass::class,
            classes = Types(
                """
                class JacksonClass {
                  final Color colour;
                  final Shape shape;
                
                  JacksonClass({
                    required this.colour,
                    required this.shape,
                  });
                
                }
                """.trimIndent()
            ),
            enums = Types(
                """
                enum Color {
                  RED, // = r
                  GREEN, // = g
                }
                """.trimIndent(),
                """
                enum Shape {
                  RECTANGLE, // = rect
                  CIRCLE, // = circ
                }
                """.trimIndent(),
            )
        )
    }
})
