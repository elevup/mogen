![Mogen logo](logo.svg "Mogen logo")

Mogen is a small library that converts Kotlin's models to other programming languages:

- Dart 2.12+,
- OpenApi 3.0 YML (generates `schemas` node),
- Swift,
- TypeScript.

### Motivation

In elevup we usually write backends in Kotlin. Unfortunately, frontends are whole other story. So the main goal was to
find a way to transfer the knowledge efficiently, and we went for OpenApi. But rewriting each diff in models to OpenApi
was more-less pain and literally no one wanted to do it.

Thanks to Mogen we were able to take all Kotlin models and convert them to OpenApi format. We felt that it was not 
enough cause all frontend devs had to rewrite them once again. So eventually we added convertors to more languages.

At this moment we generate frontend API models from backend models.

### Features
Supported features:
 - primitives - conversion from Kotlin to target language,
 - homogenous iterables - select collections and arrays are converted,
 - null-safety,
 - enums - including custom serialisation name,
 - classes - all public properties are transformed,
 - nested classes,
 - maps with `String` keys - maps with any other key type are converted to "any" type (JSON allows only string keys),
 - optional wrappers - distinguish omitted property from `null` (e.g. PATCH requests),
 - sealed classes/interfaces - one level of sealed hierarchy (a class can have at most one sealed parent),
 - type aliases - only trivial cases, for example `typealias UserId = Long`.

Unsupported features:
 - maps with non-`String` keys,
 - generics (except registered optional wrappers),
 - inheritance (other than sealed hierarchies),
 - abstract classes,
 - packages (= output is supposed to be printed into one file).

Have a look at `sample/src/main` where in `kotlin` folder are source files and in `resources` folder 
you can find generated output.

## Latest version

Library is available on Maven Central.

![Latest version](https://img.shields.io/maven-central/v/com.elevup.mogen/mogen "Latest lib version")
```groovy
implementation 'com.elevup.mogen:mogen:$version' // Version 2.X.Y
implementation 'com.elevup:mogen:$version' // Version 1.X.Y
```

Since version `2.X.Y` annotation processors are published as separate artifacts, so you only pull in the annotation
library you actually use. Add the ones you need:

```groovy
implementation 'com.elevup.mogen:mogen-jackson:$version'                 // @JsonProperty, @JsonValue
implementation 'com.elevup.mogen:mogen-jakarta-validation:$version'      // @Size, @Min, @Max, @Pattern, ...
implementation 'com.elevup.mogen:mogen-kotlinx-serialization:$version'   // @SerialName
implementation 'com.elevup.mogen:mogen-skrutiny:$version'                // @Length, @Min, @Max, @Pattern
```

`DeprecatedAnnotationProcessor` (Kotlin's own `@Deprecated`) ships with the core artifact.

## How to use
First you need to gather all classes that need to be transformed. Simple create list of them.

```kotlin
val classes: List<KClass<*>> = listOf(Foo::class, Bar::class)
```

### Generate definitions
Depending on desired output pick a generator:

- `DartGenerator` ... Dart 2.12+
- `OpenApiGenerator` ... OpenApi 3.0
- `SwiftGenerator` ... Swift
- `TypeScriptGenerator` ... TypeScript

```kotlin
val generator: CachedGenerator = TypeScriptGenerator()
    .appendClass(SomeClass::class)
    .appendClasses(listOf(Foo::class, Bar::class))

// Access generated definitions
val classes = generator.generatedClasses
val enums = generator.generatedEnums
val types = generator.generatedTypeAliases
```

### Type aliases
When adding classes Mogen automatically detects presence of type aliases. However, in some cases
additional mapping is required. Imagine you want to serialise `LocalDateTime`. In some implementations it will become
`String` (ISO representation), in some `Long` (UNIX timestamp). This conversion is out of the scope of this
library but your frontend models must be compatible. In order to do so, you must define target language type.

For example in `TypeScript` date is represented by `Date`. To map it correctly, use `appendTypealias` method:

```kotlin
TypeScriptGenerator()
    .appendClass(User::class)
    .appendTypealias(Typealias(localClass = LocalDateTime::class, name = "Date"))
```

Input (Kotlin):
```kotlin
typealias UserId = Long

data class User(
    val id: UserId,
    val birthday: LocalDateTime,
)
```
Output (TypeScript):
```typescript
export type LocalDateTime = Date
export type UserId = number // copied automatically

export interface User {
    birthday: LocalDateTime; // Kotlin's class name is kept, type is created
    id: UserId;
}
```

### Maps
Maps are supported only with `String` (`CharSequence`) keys, since JSON objects allow only string keys. Maps with any
other key type fall back to "any" type of the target language.

Input (Kotlin):
```kotlin
data class ClassWithMaps(
    val dataClassString: Map<DataClass, String>,
    val stringAny: Map<String, Any>,
    val stringLong: Map<String, Long>,
)
```

Output (TypeScript):
```typescript
export interface ClassWithMaps {
  dataClassString: any;
  stringAny: Record<string, any>;
  stringLong: Record<string, number>;
}
```

### Optional wrappers (PATCH)
In PATCH requests backend needs to know whether a property was omitted (= keep current value) or explicitly set
to `null` (= clear the value). This is typically modelled by a wrapper:

```kotlin
sealed interface Optional<out T> {
    data class Some<out T>(val value: T) : Optional<T>
    data object None : Optional<Nothing>
}
```

Register the wrapper and Mogen generates property of type `T` that may be omitted. The wrapper itself is not
generated, (de)serialization (`None` = omitted, `Some(x)` = `x`) is up to you.

```kotlin
TypeScriptGenerator()
    .appendOptionalWrapper(Optional::class)
    .appendClass(PatchFormRequest::class)
```

Input (Kotlin):
```kotlin
data class PatchFormRequest(
    val cin: Optional<String?>,
    val zip: Optional<String>,
)
```

Output:

| Language   | `cin: Optional<String?>`             | `zip: Optional<String>`       |
|------------|--------------------------------------|-------------------------------|
| TypeScript | `cin?: string \| null;`              | `zip?: string;`               |
| Swift      | `let cin: String??`                  | `let zip: String?`            |
| Dart       | `final String? cin;`                 | `final String? zip;`          |
| OpenApi    | `type: string`, `nullable: true`     | `type: string`, `nullable: false` |

- TypeScript: omitted = `undefined`, `null` = `null`.
- Swift: outer `nil` = omitted, `.some(nil)` = `null`. Synthesized `Codable` does not distinguish them, custom decoding
  is needed.
- Dart cannot express the difference in type, so the property is nullable, not `required` in constructor and
  documented by a comment.
- OpenApi: omitted = property is not required.

### Sealed classes
Sealed classes and interfaces are converted to a parent type and its subtypes. Appending any class of the hierarchy
generates the whole hierarchy (parent and all subclasses, including `object` subclasses). Properties overridden in
subclasses are declared only in the parent. Nested sealed hierarchies (sealed class with sealed parent) and multiple sealed parents
are not supported and throw `IllegalStateException`.

Input (Kotlin):
```kotlin
sealed interface SealedClass {
    val id: Long
    val type: String

    data class A(override val id: Long, val customA: String) : SealedClass {
        override val type = "A"
    }

    data class B(override val id: Long, val customB: String) : SealedClass {
        override val type = "B"
    }
}
```

Output (TypeScript):
```typescript
export interface SealedClass {
  id: number;
  type: string;
}

export interface SealedClassA extends SealedClass {
  customA: string;
}

export interface SealedClassB extends SealedClass {
  customB: string;
}
```

Dart output uses `sealed class` / `extends`, Swift and OpenApi have their own representation (OpenApi uses `allOf`).

### Indents
Everyone uses different indentation rules. To make it easier for you Mogen lets you configure basic indentation unit.
To adjust default indentation pass your own `Indentation` in generator's constructor. You can create your own instance or
use one of pre-defined indents:
- `GenericIndents` - designed for Dart, Swift and TypeScript,
- `OpenApiIndents` - designed for OpenApi.

```kotlin
TypeScriptGenerator(indents = GenericIndents(indent = "\t")) // For 'tabs' crew
OpenApiGenerator(indents = OpenApiIndents(indent = "  ")) // Fore 'spaces' crew
```

### Pretty print
In case you do not want to deal with formatting use `Printer` to convert generated definitions to single `String`
Library comes with bundled:
 - `GenericPrinter` that deals well with Dart, Swift and TypeScript,
 - `OpenApiPrinter` that is designed for OpenApi.

```kotlin
val allDefinitions: String = generator.print(GenericPrinter)
```

### Annotation processors
Sometimes it's useful to keep some metadata. Imagine you have just deprecated some property in backed, and you 
want to let others know that they should migrate their code. In that case you want to
use `DeprecatedAnnotationProcessor`! Annotation processors are passed in generator's constructor.

#### Deprecation
```kotlin
TypeScriptGenerator(annotationProcessors = listOf(DeprecatedAnnotationProcessor())) // <- pass them here
       .appendClass(User::class)
       .appendTypeAlias(Typealias(localClass = LocalDateTime::class, name = "Date"))
       .print(GenericPrinter)
       .let { println(it) }
```

Input (Kotlin):
```kotlin
data class User(
    val id: Long,
    @Deprecated("Not time-zone bulletproof, calculate it on your own")
    val age: Int,
    val birthday: LocalDateTime,
)
```

Output (TypeScript):
```typescript
export type LocalDateTime = Date

export interface User {
  /**
   * @deprecated Not time-zone bulletproof, calculate it on your own
   */
  age: number;
  birthday: LocalDateTime;
  id: number;
}

```

#### JavaX annotations
For validations like min, max, etc. on server; during conversion it will append comment where validation constraints 
are listed.

Input (Kotlin):
```kotlin
data class User(
    val id: Long,
    @field:Size(min = 1, max = 100)
    val firstName: String,
)
```

Output (TypeScript):
```typescript
export interface User {
  /**
   * min: 1
   * max: 100
   */
  firstName: string;
  id: number;
}
```

#### Jackson annotations
Handles `@JsonProperty` and `@JsonValue` annotations that can modify name of serialised property.

Input (Kotlin):
```kotlin
data class User(
    @JsonProperty("user_id")
    val id: Long,
    @JsonProperty("tea")
    val favouriteTea: Tea? = null,
) {
    enum class Tea {
        @JsonProperty("g") GREEN, 
        @JsonProperty("b") BLACK,
    }
}
```

Output (TypeScript):
```typescript
export enum UserTea {
  GREEN = 'g',
  BLACK = 'b',
}

export interface User {
    tea: UserTea | null;
    user_id: number;
}
```

### How to collect all models
If all models are in one package then [Reflections](https://github.com/ronmamo/reflections) library can be helpful! Then you can use this code to
get all classes:

```kotlin
val sourcePackage = "com.foo.bar" // Enter correct value
val reflections = Reflections(
    ConfigurationBuilder()
        .filterInputsBy(FilterBuilder().includePackage(sourcePackage))
        .setUrls(ClasspathHelper.forPackage(sourcePackage))
        .setScanners(SubTypesScanner(false))
)

val typeList = reflections.getSubTypesOf(Object::class.java) + reflections.getSubTypesOf(Enum::class.java)
val classes = typeList.map { c -> c.kotlin }.distinct()
```

## Known issues

- **Swift: sealed parent cannot be used as a property type.** Sealed parent is generated as a `protocol`, which is not
  `Codable`, so a `Codable` struct holding it (e.g. `let parent: SealedClass`) does not compile. Subclasses themselves
  (`SealedClassA`, ...) work fine.
- **OpenApi: sealed hierarchies have no `discriminator`.** Parent is generated as `allOf` + `oneOf` of its subclasses,
  which is valid, but client generators may not produce polymorphic types from it.

## License

    Copyright 2021 elevup

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
