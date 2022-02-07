package com.elevup.processors

import com.elevup.annotation.DeprecatedAnnotationProcessor
import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.models.Types
import com.elevup.models.appendAndExpectOutput
import io.kotest.core.spec.style.StringSpec

data class DeprecatedClass(
    @Deprecated(message = "/* \"Id\" */ is deprecated")
    val id: Long
)

enum class DeprecatedEnum {
    @Deprecated(message = "Ain't not time for that!")
    HELLO,
    HI
}

class DeprecatedTests : StringSpec({
    val processors = listOf(DeprecatedAnnotationProcessor())

    "TypeScript class property deprecation" {
        TypeScriptGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedClass::class,
            classes = Types(
                """
                export interface DeprecatedClass {
                  /**
                   * @deprecated /* "Id" *\/ is deprecated
                   */
                  id: number;
                }
                """.trimIndent()
            )
        )
    }

    "TypeScript enum member deprecation" {
        TypeScriptGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedEnum::class,
            enums = Types(
                """
                export enum DeprecatedEnum {
                  /**
                   * @deprecated Ain't not time for that!
                   */
                  HELLO = 'HELLO',
                  HI = 'HI',
                }
                """.trimIndent()
            )
        )
    }

    "Dart class property deprecation" {
        DartGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedClass::class,
            classes = Types(
                """
                class DeprecatedClass {
                  /**
                   * @deprecated /* "Id" *\/ is deprecated
                   */
                  final int id;
                
                  DeprecatedClass({
                    required this.id,
                  });
                
                }
                """.trimIndent()
            )
        )
    }

    "Dart enum member deprecation" {
        DartGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedEnum::class,
            enums = Types(
                """
                enum DeprecatedEnum {
                  /**
                   * @deprecated Ain't not time for that!
                   */
                  HELLO, // = HELLO
                  HI, // = HI
                }
                """.trimIndent()
            )
        )
    }

    "Swift class property deprecation" {
        SwiftGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedClass::class,
            classes = Types(
                """
                struct DeprecatedClass: Codable {
                  @available(*, deprecated, message: "/* \"Id\" */ is deprecated")
                  let id: Int
                }
                """.trimIndent()
            )
        )
    }

    "Swift enum member deprecation" {
        SwiftGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = DeprecatedEnum::class,
            enums = Types(
                """
                enum DeprecatedEnum: String, Codable {
                  @available(*, deprecated, message: "Ain't not time for that!")
                  case hello = "HELLO"
                  case hi = "HI"
                }
                """.trimIndent()
            )
        )
    }

})
