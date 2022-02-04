package com.elevup

import com.elevup.annotation.DeprecatedAnnotationProcessor
import com.elevup.annotation.JavaXAnnotationProcessor
import com.elevup.generator.print
import com.elevup.languages.dart.DartGenerator
import com.elevup.languages.openapi.OpenApiGenerator
import com.elevup.languages.swift.SwiftGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.Typealias
import com.elevup.printer.GenericPrinter
import com.elevup.printer.OpenApiPrinter
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.reflect.KClass

private val processors = listOf(
    JavaXAnnotationProcessor(),
    DeprecatedAnnotationProcessor()
)

fun main() {
    val sourcePackage = "com.elevup.models"
    val reflections = Reflections(
        ConfigurationBuilder()
            .filterInputsBy(FilterBuilder().includePackage(sourcePackage))
            .setUrls(ClasspathHelper.forPackage(sourcePackage))
            .setScanners(SubTypesScanner(false))
    )

    val typeList = reflections.getSubTypesOf(Object::class.java) + reflections.getSubTypesOf(Enum::class.java)
    val classes = typeList.map { c -> c.kotlin }.distinct()

    generateDart(classes)
    generateOpenApi(classes)
    generateSwift(classes)
    generateTypeScript(classes)

}

private fun generateDart(
    classes: List<KClass<*>>,
    workingDir: String = System.getProperty("user.dir")
) {
    with(DartGenerator(annotationProcessors = processors)) {
        appendClasses(classes)
        appendTypeAliases(
            listOf(
                Typealias(LocalDateTime::class, "String", "yyyy-MM-dd'T'HH:mm:ss'Z'"),
                Typealias(BigDecimal::class, "String", null),
            )
        )
    }.print(GenericPrinter).let { code ->
        val file = File(workingDir, "sample/src/main/resources/dart.dart")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        file.writeText(code)
    }
}

private fun generateOpenApi(
    classes: List<KClass<*>>,
    workingDir: String = System.getProperty("user.dir")
) {
    with(OpenApiGenerator(annotationProcessors = processors)) {
        appendClasses(classes)
        appendTypeAliases(
            listOf(
                Typealias(LocalDateTime::class, "string", "yyyy-MM-dd'T'HH:mm:ss'Z'"),
                Typealias(BigDecimal::class, "string", null),
            )
        )
    }.print(OpenApiPrinter()).let { code ->
        val templateFile = File(workingDir, "sample/src/main/resources/openapi/template.yml")
        val targetFile = File(workingDir, "sample/src/main/resources/openapi.yml")

        targetFile.writeText(
            templateFile.readText().replace("[SCHEMAS]", code.prependIndent("  "))
        )
    }

}

private fun generateSwift(
    classes: List<KClass<*>>,
    workingDir: String = System.getProperty("user.dir")
) {
    with(SwiftGenerator(annotationProcessors = processors)) {
        appendClasses(classes)
        appendTypeAliases(
            listOf(
                Typealias(LocalDateTime::class, "String", "yyyy-MM-dd'T'HH:mm:ss'Z'"),
                Typealias(BigDecimal::class, "Decimal", null),
            )
        )
    }.print(GenericPrinter).let { code ->
        val file = File(workingDir, "sample/src/main/resources/swift.swift")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        file.writeText(code)
    }

}

private fun generateTypeScript(
    classes: List<KClass<*>>,
    workingDir: String = System.getProperty("user.dir")
) {
    with(TypeScriptGenerator(annotationProcessors = processors)) {
        appendClasses(classes)
        appendTypeAliases(
            listOf(
                Typealias(LocalDateTime::class, "Date", "yyyy-MM-dd'T'HH:mm:ss'Z'"),
                Typealias(BigDecimal::class, "string", null),
            )
        )
    }.print(GenericPrinter).let { code ->
        val file = File(workingDir, "sample/src/main/resources/typescript.ts")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(code)
    }
}
