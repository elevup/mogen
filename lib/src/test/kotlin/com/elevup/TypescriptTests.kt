package com.elevup

import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TypescriptTests : StringSpec({
    fun getGenerator() = TypeScriptGenerator(annotationProcessors = emptyList(), indents = GenericIndents())


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
})
