plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    application
}

group = "ru.kosolapov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // Official kotlin grammar tools not published in maven central
    flatDir {
        dirs("parser")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin.spec.grammar.tools:kotlin-grammar-tools:0.1")

    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1")

    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("ru.kosolapov.analyzer.AnalyzerCli")
}

kotlin {
    jvmToolchain(21)
}
