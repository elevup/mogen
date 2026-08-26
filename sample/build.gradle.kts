plugins {
    application
    alias(libs.plugins.kotlin.jvm)
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
    implementation(libs.reflections)

    compileOnly(libs.jackson.annotations)
    compileOnly(libs.hibernate.validator)
}