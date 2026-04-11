plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    application
}

group = "com.julia.discordbot"
version = "1.0-SNAPSHOT"

val ktorVersion = "3.4.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Discord Bot - Kord
    implementation("dev.kord:kord-core:0.18.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // Ktor Server (Dashboard)
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
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
