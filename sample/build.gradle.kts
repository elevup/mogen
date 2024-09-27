plugins {
    application
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":lib"))
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.12.4")
    compileOnly("org.hibernate.validator:hibernate-validator:6.2.0.Final")
}