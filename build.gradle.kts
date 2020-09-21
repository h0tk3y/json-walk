plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "com.h0tk3y"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
    testImplementation(kotlin("test-junit"))
}

application {
    mainClassName = "com.h0tk3y.jsonwalk.MainKt"
}