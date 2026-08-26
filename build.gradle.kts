buildscript {
    val kotlin_version = "2.4.10"

    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.37.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
