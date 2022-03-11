@file:Suppress("SpellCheckingInspection")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
}

group = "me.weiwen.tsundoku"
version = "1.0.0"

repositories {
    jcenter()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

    // bStats
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }

    // MineDown
    maven { url = uri("https://repo.minebench.de/") }

    mavenLocal()
}


dependencies {
    implementation(kotlin("stdlib-jdk8", "1.6.0"))

    // Deserialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("com.charleskorn.kaml:kaml:0.33.0")

    // Paper
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")

    // bStats
    implementation("org.bstats", "bstats-bukkit", "1.8")

    // MineDown
    implementation("de.themoep", "minedown-adventure", "1.7.0-SNAPSHOT")
}

bukkit {
    main = "me.weiwen.tsundoku.Tsundoku"
    name = "Tsundoku"
    version = "1.0.0"
    description = "Easily build custom books for your Minecraft server"
    apiVersion = "1.16"
    author = "Goh Wei Wen <goweiwen@gmail.com>"
    website = "weiwen.me"

    depend = listOf()
    softDepend = listOf()

    commands {
        register("tsundoku") {
            description = "Manages the Tsundoku plugin"
            usage = "/<command> reload"
            permission = "tsundoku.admin"
        }
        register("book") {
            description = "Read a book"
            usage = "/<command> <book path>"
            permission = "tsundoku.book"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.languageVersion = "1.6"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xuse-experimental=org.jetbrains.kotlinx.serialization.ExperimentalSerializationApi"
    )
}

tasks.withType<ShadowJar> {
    classifier = null
}
