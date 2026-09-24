package com.elevup

import com.elevup.languages.openapi.OpenApiGenerator
import com.elevup.model.ComposerConfig
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

    "maps" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithMaps::class,
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
                """.trimIndent(),
                """
                ClassWithMaps:
                  type: object
                  properties:
                    dataClassString: {}
                    stringAny:
                      type: object
                      additionalProperties: true
                      nullable: false
                    stringDataClass:
                      type: object
                      additionalProperties:
                        ${dollar}ref: '#/components/schemas/DataClass'
                      nullable: false
                    stringLong:
                      type: object
                      additionalProperties:
                        type: number
                        format: int64
                        nullable: false
                      nullable: false
                """.trimIndent()
            )
        )
    }

    "sealed" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedUsage::class,
            classes = Types(
                """
                SealedUsage:
                  type: object
                  properties:
                    parent:
                      ${dollar}ref: '#/components/schemas/SealedClass'
                    typeA:
                      ${dollar}ref: '#/components/schemas/SealedClassA'
                    typeB:
                      ${dollar}ref: '#/components/schemas/SealedClassB'
                """.trimIndent(),
                """
                SealedClass:
                  allOf:
                    - type: object
                      properties:
                        id:
                          type: number
                          format: int64
                          nullable: false
                        type:
                          type: string
                          nullable: false
                    - oneOf:
                      - ${dollar}ref: '#/components/schemas/SealedClassA'
                      - ${dollar}ref: '#/components/schemas/SealedClassB'
                """.trimIndent(),
                """
                SealedClassA:
                  type: object
                  properties:
                    customA:
                      type: string
                      nullable: false
                    id:
                      type: number
                      format: int64
                      nullable: false
                    type:
                      type: string
                      nullable: false
                """.trimIndent(),
                """
                SealedClassB:
                  type: object
                  properties:
                    customB:
                      type: string
                      nullable: false
                    id:
                      type: number
                      format: int64
                      nullable: false
                    type:
                      type: string
                      nullable: false
                """.trimIndent()
            )
        )
    }

    "sealed without properties and with object" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedWithoutProperties.Empty::class,
            classes = Types(
                """
                SealedWithoutPropertiesEmpty:
                  type: object
                  properties: {}
                """.trimIndent(),
                """
                SealedWithoutProperties:
                  allOf:
                    - type: object
                      properties: {}
                    - oneOf:
                      - ${dollar}ref: '#/components/schemas/SealedWithoutPropertiesEmpty'
                      - ${dollar}ref: '#/components/schemas/SealedWithoutPropertiesValue'
                """.trimIndent(),
                """
                SealedWithoutPropertiesValue:
                  type: object
                  properties:
                    value:
                      type: number
                      format: int32
                      nullable: false
                """.trimIndent(),
            )
        )
    }

    "sealed with inherited constructor property" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedWithConstructorProperty.Child::class,
            classes = Types(
                """
                SealedWithConstructorPropertyChild:
                  type: object
                  properties:
                    id:
                      type: number
                      format: int64
                      nullable: false
                    name:
                      type: string
                      nullable: false
                """.trimIndent(),
                """
                SealedWithConstructorProperty:
                  allOf:
                    - type: object
                      properties:
                        id:
                          type: number
                          format: int64
                          nullable: false
                    - oneOf:
                      - ${dollar}ref: '#/components/schemas/SealedWithConstructorPropertyChild'
                """.trimIndent(),
            )
        )
    }
})
