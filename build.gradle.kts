group = "me.minidigger"
version = "1.0-SNAPSHOT"

plugins {
    java
    application
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
    testImplementation("junit:junit:5.7.1")
}
