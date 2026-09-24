package com.elevup

import com.elevup.languages.swift.SwiftGenerator
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

    "maps" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithMaps::class,
            classes = Types(
                """
                struct DataClass: Codable {
                  let id: Int
                  let name: String?
                }
                """.trimIndent(),
                """
                struct ClassWithMaps: Codable {
                  let dataClassString: AnyCodable
                  let stringAny: [String: AnyCodable]
                  let stringDataClass: [String: DataClass]
                  let stringLong: [String: Int]
                }
                """.trimIndent()
            )
        )
    }

    "sealed" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedUsage::class,
            classes = Types(
                """
                struct SealedUsage: Codable {
                  let parent: SealedClass
                  let typeA: SealedClassA
                  let typeB: SealedClassB
                }
                """.trimIndent(),
                """
                protocol SealedClass {
                  var id: Int { get }
                  var type: String { get }
                }
                """.trimIndent(),
                """
                struct SealedClassA: SealedClass, Codable {
                  let customA: String
                  let id: Int
                  let type: String
                }
                """.trimIndent(),
                """
                struct SealedClassB: SealedClass, Codable {
                  let customB: String
                  let id: Int
                  let type: String
                }
                """.trimIndent(),
            )
        )
    }

    "sealed with inherited constructor property" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedWithConstructorProperty.Child::class,
            classes = Types(
                """
                struct SealedWithConstructorPropertyChild: SealedWithConstructorProperty, Codable {
                  let id: Int
                  let name: String
                }
                """.trimIndent(),
                """
                protocol SealedWithConstructorProperty {
                  var id: Int { get }
                }
                """.trimIndent(),
            )
        )
    }

    "optional wrapper" {
        getGenerator().appendOptionalWrapper(Optional::class).appendAndExpectOutput(
            clazz = PatchFormRequest::class,
            classes = Types(
                """
                struct DataClass: Codable {
                  let id: Int
                  let name: String?
                }
                """.trimIndent(),
                """
                struct PatchFormRequest: Codable {
                  /**
                   * optional: nil = omitted, .some(nil) = null
                   */
                  let address: DataClass??
                  /**
                   * optional: nil = omitted, .some(nil) = null
                   */
                  let cin: String??
                  /**
                   * optional: nil = omitted
                   */
                  let tags: [String]?
                  /**
                   * optional: nil = omitted
                   */
                  let zip: String?
                }
                """.trimIndent(),
            )
        )
    }
})
