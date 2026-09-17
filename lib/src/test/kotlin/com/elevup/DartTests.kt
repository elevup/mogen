package com.elevup

import com.elevup.languages.dart.DartGenerator
import com.elevup.model.ComposerConfig
import com.elevup.model.GenericIndents
import com.elevup.models.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DartTests : StringSpec({
    fun getGenerator(config: ComposerConfig = ComposerConfig()) = DartGenerator(
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
                "typedef UserId = int;"
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

    "nested class with prefixes and postfixes" {
        getGenerator(config = ComposerConfig(typeNamePrefix = "X", typeNamePostfix = "_v2")).appendAndExpectOutput(
            clazz = Parent::class,
            classes = Types(
                """
                class XParentChild_v2 {
                  final String id;

                  XParentChild_v2({
                    required this.id,
                  });

                }
                """.trimIndent(),
                """
                class XParent_v2 {
                  final XParentChild_v2 child;
                  final int id;

                  XParent_v2({
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

    "optional type" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithOptionalType::class,
            classes = Types(
                """
                class ClassWithOptionalType {
                  final ClassWithOptionalTypeId? id;

                  ClassWithOptionalType({
                    required this.id,
                  });

                }
                """.trimIndent()
            ),
            types = Types(
                "typedef ClassWithOptionalTypeId = int;"
            )
        )
    }

    "maps" {
        getGenerator().appendAndExpectOutput(
            clazz = ClassWithMaps::class,
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
                """.trimIndent(),
                """
                class ClassWithMaps {
                  final dynamic dataClassString;
                  final Map<String, dynamic> stringAny;
                  final Map<String, DataClass> stringDataClass;
                  final Map<String, int> stringLong;

                  ClassWithMaps({
                    required this.dataClassString,
                    required this.stringAny,
                    required this.stringDataClass,
                    required this.stringLong,
                  });

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
                class SealedUsage {
                  final SealedClass parent;
                  final SealedClassA typeA;
                  final SealedClassB typeB;

                  SealedUsage({
                    required this.parent,
                    required this.typeA,
                    required this.typeB,
                  });

                }
                """.trimIndent(),
                """
                sealed class SealedClass {
                  final int id;
                  final String type;
                
                  SealedClass({
                    required this.id,
                    required this.type,
                  });
                
                }
                """.trimIndent(),
                """
                class SealedClassA extends SealedClass {
                  final String customA;
                
                  SealedClassA({
                    required this.customA,
                    required super.id,
                    required super.type,
                  });
                
                }
                """.trimIndent(),
                """
                class SealedClassB extends SealedClass {
                  final String customB;
                
                  SealedClassB({
                    required this.customB,
                    required super.id,
                    required super.type,
                  });
                
                }
                """.trimIndent(),
            )
        )
    }
})
