import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "yt.richard"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.4.31"
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.github.instagram4j:instagram4j:2.0.3")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("marcidus")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "yt.richard.marcidus.MainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}