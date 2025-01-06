plugins {
    application
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

}

dependencies {
    implementation(project(":lib"))
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.12.4")
    compileOnly("org.hibernate.validator:hibernate-validator:6.2.0.Final")
}