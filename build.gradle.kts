import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("idea")
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.30"
}

group = "com.londogard"
version = "1.0-beta"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

val kluentVersion: String by project

dependencies {
    // ND4J
    implementation("org.ejml:ejml-core:0.40")
    implementation("org.ejml:ejml-fdense:0.40")
    implementation("org.ejml:ejml-fsparse:0.40")
    implementation("org.ejml:ejml-simple:0.40")
    implementation("org.ejml:ejml-kotlin:0.40")

    // implementation("com.github.levyfan:sentencepiece-jni:v0.0.2")
    implementation("ai.djl.sentencepiece:sentencepiece:0.10.0")
    implementation("com.github.rholder:snowball-stemmer:1.3.0.581.1")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    implementation("org.apache.commons:commons-compress:1.20")

    implementation("org.codehaus.plexus:plexus-archiver:4.2.4")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.32")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/londogard/londogard-nlp-toolkit")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}