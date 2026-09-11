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

sourceSets {
    with(main.get()) {
        kotlin { srcDir("src") }
        java { srcDir("src") }
        resources { srcDir("resources") }
    }

}

dependencies {
    implementation(project(":lib"))
    implementation(project(":processors:jackson"))
    implementation(project(":processors:jakarta-validation"))
    implementation(libs.reflections)

    compileOnly(libs.hibernate.validator)
}