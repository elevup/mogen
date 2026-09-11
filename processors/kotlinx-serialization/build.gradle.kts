plugins {
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.jvm)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    api(project(":lib"))
    api(libs.kotlinx.serialization)

    testImplementation(testFixtures(project(":lib")))
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}
