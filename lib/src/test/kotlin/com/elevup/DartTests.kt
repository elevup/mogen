package com.elevup

import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DartTests : StringSpec({
    fun getGenerator() = DartGenerator(annotationProcessors = emptyList(), indents = GenericIndents())


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
                class DataClass {
                  final int id;
                  final String? name;
                
                  DataClass({
                    required this.id,
                    required this.name,
                  });
                
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
                class ClassWithTypealias {
                  final UserId id;
                
                  ClassWithTypealias({
                    required this.id,
                  });
                
                }
                """.trimIndent()
            ),
            types = Types(
                "typedef UserId = int"
            )
        )
    }

    "enum" {
        getGenerator().appendAndExpectOutput(
            clazz = SimpleEnum::class,
            enums = Types(
                """
                enum SimpleEnum {
                  RIGHT, // = RIGHT
                  LEFT, // = LEFT
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
                class ParentChild {
                  final String id;
                
                  ParentChild({
                    required this.id,
                  });
                
                }
                """.trimIndent(),
                """
                class Parent {
                  final ParentChild child;
                  final int id;
                
                  Parent({
                    required this.child,
                    required this.id,
                  });
                
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
                class ClassWithIterables {
                  final List<String> iterable;
                  final List<String?> optionalArguments;
                  final List<String?>? optionalEverything;
                  final List<String>? optionalIterable;
                
                  ClassWithIterables({
                    required this.iterable,
                    required this.optionalArguments,
                    required this.optionalEverything,
                    required this.optionalIterable,
                  });
                
                }
                """.trimIndent()
            )
        )
    }
})
