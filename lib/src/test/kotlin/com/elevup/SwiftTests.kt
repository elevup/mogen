package com.elevup

import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.ComposerConfig
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SwiftTests : StringSpec({
    fun getGenerator(config: ComposerConfig = ComposerConfig()) = SwiftGenerator(
        annotationProcessors = emptyList(),
        indents = GenericIndents(),
        config = config
    )


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

    "nested class with prefixes and postfixes" {
        getGenerator(config = ComposerConfig(typeNamePrefix = "X", typeNamePostfix = "_v2")).appendAndExpectOutput(
            clazz = Parent::class,
            classes = Types(
                """
                struct XParentChild_v2: Codable {
                  let id: String
                }
                """.trimIndent(),
                """
                struct XParent_v2: Codable {
                  let child: XParentChild_v2
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

    "optional type" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithOptionalType::class,
            classes = Types(
                """
                struct ClassWithOptionalType: Codable {
                  let id: ClassWithOptionalTypeId?
                }
                """.trimIndent()
            ),
            types = Types(
                "typealias ClassWithOptionalTypeId = Int"
            )
        )
    }
})
