package com.elevup

import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SwiftTests : StringSpec({
    fun getGenerator() = SwiftGenerator(annotationProcessors = emptyList(), indents = GenericIndents())


    "empty classes should me omitted" {
        getGenerator().appendAndExpectOutput(
            clazz = EmptyClass::class,
            classes = { it.size shouldBe 0 }
        )

    }

    "class with nullable property" {
        getGenerator().appendAndExpectOutput(
            clazz = DataClass::class,
            classes = Types(
                """
                struct DataClass: Codable {
                  let id: Int
                  let name: String?
                }
                """.trimIndent()
            )
        )
    }

    "class with simple type alias" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithTypealias::class,
            classes = Types(
                """
                struct ClassWithTypealias: Codable {
                  let id: UserId
                }
                """.trimIndent()
            ),
            types = Types(
                "typealias UserId = Int"
            )
        )
    }

    "enum" {
        getGenerator().appendAndExpectOutput(
            clazz = SimpleEnum::class,
            enums = Types(
                """
                enum SimpleEnum: String, Codable {
                  case right = "RIGHT"
                  case left = "LEFT"
                }
                """.trimIndent()
            )
        )
    }

    "nested class" {
        getGenerator().appendAndExpectOutput(
            clazz = Parent::class,
            classes = Types(
                """
                struct ParentChild: Codable {
                  let id: String
                }
                """.trimIndent(),
                """
                struct Parent: Codable {
                  let child: ParentChild
                  let id: Int
                }
                """.trimIndent()
            )
        )
    }

    "iterables" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithIterables::class,
            classes = Types(
                """
                struct ClassWithIterables: Codable {
                  let iterable: [String]
                  let optionalArguments: [String?]
                  let optionalEverything: [String?]?
                  let optionalIterable: [String]?
                }
                """.trimIndent()
            )
        )
    }
})
