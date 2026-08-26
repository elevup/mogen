plugins {
    application
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.jakarta.validation.api)
    implementation(libs.jackson.annotations)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}
