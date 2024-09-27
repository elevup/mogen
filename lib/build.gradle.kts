plugins {
    application
    id("com.vanniktech.maven.publish")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.2.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.2.1")
}
