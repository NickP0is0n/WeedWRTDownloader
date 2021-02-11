import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "me.mykolachaikovskyi"
version = "0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "net.lingala.zip4j", name = "zip4j", version = "2.6.4")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}