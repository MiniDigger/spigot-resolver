import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:30.1-jre")
    testImplementation("junit:junit:5.7.1")
}

project.setProperty("mainClassName", "me.minidigger.spigotresolver.SpigotResolver")

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "me.minidigger.spigotresolver.SpigotResolver"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}


