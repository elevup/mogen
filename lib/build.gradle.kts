plugins {
    application
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.kotlin.jvm")
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
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}
