buildscript {
    val kotlin_version = "2.1.0"

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
