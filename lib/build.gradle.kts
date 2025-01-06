plugins {
    application
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    with(main.get()) {
        kotlin { srcDir("src") }
        java { srcDir("src") }
        resources { srcDir("resources") }
    }

    with(test.get()) {
        kotlin { srcDir("test") }
        java { srcDirs("test") }
        resources { srcDir("testresources") }
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
