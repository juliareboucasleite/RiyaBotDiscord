plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "com.julia.discordbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("dev.kord:kord-core:0.18.1")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.julia.discordbot.MainKt")
}
