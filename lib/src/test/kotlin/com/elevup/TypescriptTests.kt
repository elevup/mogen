package com.elevup

import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.ComposerConfig
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TypescriptTests : StringSpec({
    fun getGenerator(config: ComposerConfig = ComposerConfig()) = TypeScriptGenerator(
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
                export interface DataClass {
                  id: number;
                  name?: string;
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
                export interface ClassWithTypealias {
                  id: UserId;
                }
                """.trimIndent()
            ),
            types = Types(
                "export type UserId = number"
            )
        )
    }

    "enum" {
        getGenerator().appendAndExpectOutput(
            clazz = SimpleEnum::class,
            enums = Types(
                """
                export enum SimpleEnum {
                  RIGHT = 'RIGHT',
                  LEFT = 'LEFT',
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
                export interface ParentChild {
                  id: string;
                }
                """.trimIndent(),
                """
                export interface Parent {
                  child: ParentChild;
                  id: number;
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
                export interface XParentChild_v2 {
                  id: string;
                }
                """.trimIndent(),
                """
                export interface XParent_v2 {
                  child: XParentChild_v2;
                  id: number;
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
                export interface ClassWithIterables {
                  iterable: string[];
                  optionalArguments: (string | null)[];
                  optionalEverything?: (string | null)[];
                  optionalIterable?: string[];
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
                export interface ClassWithOptionalType {
                  id?: ClassWithOptionalTypeId;
                }
                """.trimIndent()
            ),
            types = Types(
                "export type ClassWithOptionalTypeId = number"
            )
        )
    }

    "maps" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithMaps::class,
            classes = Types(
                """
                export interface DataClass {
                  id: number;
                  name?: string;
                }
                """.trimIndent(),
                """
                export interface ClassWithMaps {
                  dataClassString: any;
                  stringAny: Record<string, any>;
                  stringDataClass: Record<string, DataClass>;
                  stringLong: Record<string, number>;
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
                export interface SealedUsage {
                  parent: SealedClass;
                  typeA: SealedClassA;
                  typeB: SealedClassB;
                }
                """.trimIndent(),
                """
                export interface SealedClass {
                  id: number;
                  type: string;
                }
                """.trimIndent(),
                """
                export interface SealedClassA extends SealedClass {
                  customA: string;
                }
                """.trimIndent(),
                """
                export interface SealedClassB extends SealedClass {
                  customB: string;
                }
                """.trimIndent(),
            )
        )
    }

    "sealed hierarchy is discovered from subclass" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedClass.A::class,
            classes = Types(
                """
                export interface SealedClassA extends SealedClass {
                  customA: string;
                }
                """.trimIndent(),
                """
                export interface SealedClass {
                  id: number;
                  type: string;
                }
                """.trimIndent(),
                """
                export interface SealedClassB extends SealedClass {
                  customB: string;
                }
                """.trimIndent(),
            )
        )
    }

    "sealed without properties and with object" {
        getGenerator().appendAndExpectOutput(
            clazz = SealedWithoutProperties.Empty::class,
            classes = Types(
                """
                export interface SealedWithoutPropertiesEmpty extends SealedWithoutProperties {
                }
                """.trimIndent(),
                """
                export interface SealedWithoutProperties {
                }
                """.trimIndent(),
                """
                export interface SealedWithoutPropertiesValue extends SealedWithoutProperties {
                  value: number;
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
                export interface SealedWithConstructorPropertyChild extends SealedWithConstructorProperty {
                  name: string;
                }
                """.trimIndent(),
                """
                export interface SealedWithConstructorProperty {
                  id: number;
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
                export interface DataClass {
                  id: number;
                  name?: string;
                }
                """.trimIndent(),
                """
                export interface PatchFormRequest {
                  address?: DataClass | null;
                  cin?: string | null;
                  tags?: string[];
                  zip?: string;
                }
                """.trimIndent(),
            )
        )
    }
})
