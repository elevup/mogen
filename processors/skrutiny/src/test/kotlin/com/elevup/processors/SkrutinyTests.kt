package com.elevup.processors

import com.elevup.annotation.SkrutinyAnnotationProcessor
import com.elevup.languages.openapi.OpenApiGenerator
import com.elevup.languages.ts.TypeScriptGenerator
import com.elevup.model.OpenApiIndents
import com.elevup.models.Types
import com.elevup.models.appendAndExpectOutput
import com.elevup.skrutiny.annotations.Length
import com.elevup.skrutiny.annotations.Max
import com.elevup.skrutiny.annotations.Min
import com.elevup.skrutiny.annotations.Pattern
import io.kotest.core.spec.style.StringSpec

data class SkrutinyClass(
    @Min("1")
    @Max("100")
    val age: Int,
    @Length(min = 2, max = 32)
    val name: String,
    @Pattern(regex = "[a-z]+")
    val nickname: String,
    @Length(min = 1)
    val tag: String,
)

class SkrutinyTests : StringSpec({
    val processors = listOf(SkrutinyAnnotationProcessor())

    "TypeScript Skrutiny constraints" {
        TypeScriptGenerator(annotationProcessors = processors).appendAndExpectOutput(
            clazz = SkrutinyClass::class,
            classes = Types(
                """
                export interface SkrutinyClass {
                  /**
                   * min: 1
                   * max: 100
                   */
                  age: number;
                  /**
                   * min: 2
                   * max: 32
                   */
                  name: string;
                  /**
                   * regex: [a-z]+
                   */
                  nickname: string;
                  /**
                   * min: 1
                   */
                  tag: string;
                }
                """.trimIndent()
            )
        )
    }

    "OpenApi Skrutiny constraints" {
        OpenApiGenerator(annotationProcessors = processors, indents = OpenApiIndents()).appendAndExpectOutput(
            clazz = SkrutinyClass::class,
            classes = Types(
                """
                SkrutinyClass:
                  type: object
                  properties:
                    age:
                      type: number
                      format: int32
                      nullable: false
                      minimum: 1
                      maximum: 100
                    name:
                      type: string
                      nullable: false
                      minLength: 2
                      maxLength: 32
                    nickname:
                      type: string
                      nullable: false
                      pattern: '[a-z]+'
                    tag:
                      type: string
                      nullable: false
                      minLength: 1
                """.trimIndent()
            )
        )
    }
})
