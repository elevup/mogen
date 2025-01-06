package com.elevup.processors

import com.elevup.generator.annotation.GeneratorIgnore
import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.models.Types
import com.elevup.models.appendAndExpectOutput
import io.kotest.core.spec.style.StringSpec

data class IgnoreClass(
    val id: Long,
    @GeneratorIgnore
    val name: String,
)

enum class IgnoreEnum {
    @GeneratorIgnore
    HELLO,
    HI
}

class IgnoreTests : StringSpec({

    "TypeScript class property deprecation" {
        TypeScriptGenerator().appendAndExpectOutput(
            clazz = IgnoreClass::class,
            classes = Types(
                """
                export interface IgnoreClass {
                  id: number;
                }
                """.trimIndent()
            )
        )
    }

    "TypeScript enum member deprecation" {
        TypeScriptGenerator().appendAndExpectOutput(
            clazz = IgnoreEnum::class,
            enums = Types(
                """
                export enum IgnoreEnum {
                  HI = 'HI',
                }
                """.trimIndent()
            )
        )
    }

    "Dart class property deprecation" {
        DartGenerator().appendAndExpectOutput(
            clazz = IgnoreClass::class,
            classes = Types(
                """
                class IgnoreClass {
                  final int id;
                
                  IgnoreClass({
                    required this.id,
                  });
                
                }
                """.trimIndent()
            )
        )
    }

    "Dart enum member deprecation" {
        DartGenerator().appendAndExpectOutput(
            clazz = IgnoreEnum::class,
            enums = Types(
                """
                enum IgnoreEnum {
                  HI, // = HI
                }
                """.trimIndent()
            )
        )
    }

    "Swift class property deprecation" {
        SwiftGenerator().appendAndExpectOutput(
            clazz = IgnoreClass::class,
            classes = Types(
                """
                struct IgnoreClass: Codable {
                  let id: Int
                }
                """.trimIndent()
            )
        )
    }

    "Swift enum member deprecation" {
        SwiftGenerator().appendAndExpectOutput(
            clazz = IgnoreEnum::class,
            enums = Types(
                """
                enum IgnoreEnum: String, Codable {
                  case hi = "HI"
                }
                """.trimIndent()
            )
        )
    }

})
