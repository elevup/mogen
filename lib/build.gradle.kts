plugins {
    application
    `java-test-fixtures`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.jvm)
}


sourceSets {
    main {
        kotlin { srcDir("src/main/kotlin") }
        java { srcDir("src/main/java") }
        resources { srcDir("src/main/resources") }
    }

    test {
        kotlin { srcDir("src/test/kotlin") }
        java { srcDirs("src/test/java") }
        resources { srcDir("src/test/resources") }
    }
}

// Test fixtures exist only for the :processors:* modules' tests, they are never published
(components["java"] as AdhocComponentWithVariants).run {
    withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.reflect)

    testFixturesApi(libs.kotest.assertions.core)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}
