package com.elevup

import com.elevup.languages.openapi.OpenApiGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.ComposerConfig
import com.elevup.model.GenericIndents
import com.elevup.model.OpenApiIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OpenApiTests : StringSpec({
    val dollar = "$"
    fun getGenerator(config: ComposerConfig = ComposerConfig()) = OpenApiGenerator(
        annotationProcessors = emptyList(),
        indents = OpenApiIndents(),
        config = config,
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
                DataClass:
                  type: object
                  properties:
                    id:
                      type: number
                      format: int64
                      nullable: false
                    name:
                      type: string
                      nullable: true
                """.trimIndent()
            )
        )
    }

    "class with simple type alias" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithTypealias::class,
            classes = Types(
                """
                ClassWithTypealias:
                  type: object
                  properties:
                    id:
                      ${dollar}ref: '#/components/schemas/UserId'
                """.trimIndent()
            ),
            types = Types(
                """
                UserId:
                  type: number
                  format: int64
                  nullable: false
                """.trimIndent()
            )
        )
    }

    "enum" {
        getGenerator().appendAndExpectOutput(
            clazz = SimpleEnum::class,
            enums = Types(
                """
                SimpleEnum:
                  type: string
                  enum: [ RIGHT, LEFT, ]
                """.trimIndent()
            )
        )
    }

    "nested class" {
        getGenerator().appendAndExpectOutput(
            clazz = Parent::class,
            classes = Types(
                """
                ParentChild:
                  type: object
                  properties:
                    id:
                      type: string
                      nullable: false
                """.trimIndent(),
                """
                Parent:
                  type: object
                  properties:
                    child:
                      ${dollar}ref: '#/components/schemas/ParentChild'
                    id:
                      type: number
                      format: int64
                      nullable: false
                """.trimIndent()
            )
        )
    }

    "nested class with prefixes and postfixes" {
        getGenerator(config = ComposerConfig(typeNamePrefix = "X", typeNamePostfix = "_v2")).appendAndExpectOutput(
            clazz = Parent::class,
            classes = Types(
                """
                XParentChild_v2:
                  type: object
                  properties:
                    id:
                      type: string
                      nullable: false
                """.trimIndent(),
                """
                XParent_v2:
                  type: object
                  properties:
                    child:
                      ${dollar}ref: '#/components/schemas/XParentChild_v2'
                    id:
                      type: number
                      format: int64
                      nullable: false
                """.trimIndent()
            )
        )
    }

    "iterables" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithIterables::class,
            classes = Types(
                """
                ClassWithIterables:
                  type: object
                  properties:
                    iterable:
                      type: array
                      nullable: false
                      items:
                        type: string
                        nullable: false
                    optionalArguments:
                      type: array
                      nullable: false
                      items:
                        type: string
                        nullable: true
                    optionalEverything:
                      type: array
                      nullable: true
                      items:
                        type: string
                        nullable: true
                    optionalIterable:
                      type: array
                      nullable: true
                      items:
                        type: string
                        nullable: false
                """.trimIndent()
            )
        )
    }

    "optional type" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithOptionalType::class,
            classes = Types(
                """
                ClassWithOptionalType:
                  type: object
                  properties:
                    id:
                      nullable: true
                      allOf:
                       - ${dollar}ref: '#/components/schemas/ClassWithOptionalTypeId'
                """.trimIndent()
            ),
            types = Types(
                """
                ClassWithOptionalTypeId:
                  type: number
                  format: int64
                  nullable: false
                """.trimIndent()
            )
        )
    }
})
