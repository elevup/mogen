package com.elevup.models

import com.elevup.generator.CachedGenerator
import com.elevup.model.GeneratedType
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.reflect.KClass

inline fun CachedGenerator.appendAndExpectOutput(
    clazz: KClass<*>,
    classes: (List<GeneratedType>) -> Unit = { it.size shouldBe 0 },
    enums: (List<GeneratedType>) -> Unit = { it.size shouldBe 0 },
    types: (List<GeneratedType>) -> Unit = { it.size shouldBe 0 }
) {
    appendClass(clazz)

    classes(generatedClasses)
    enums(generatedEnums)
    types(generatedTypeAliases)
}

inline fun Types(vararg codes: String) = { list: List<GeneratedType> ->
    val expectedCodes = codes.toList()
    list.size shouldBe expectedCodes.size

    list.forEach {
        expectedCodes shouldContain it.code
    }
}
