plugins {
    application
    id("org.jetbrains.kotlin.jvm")
}

application {
    mainClass.set("com.elevup.MainKt")
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
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

}

dependencies {
    implementation(project(":lib"))
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.19.0")
    compileOnly("org.hibernate.validator:hibernate-validator:9.1.3.Final")
}