import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("org.jetbrains.dokka") version "1.4.20"
}

group = "com.londogard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/mipt-npm/scientifik")
}

val kluentVersion: String by project

dependencies {
    // ND4J
    implementation("org.nd4j:nd4j-api:1.0.0-beta7")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
    implementation("org.deeplearning4j:deeplearning4j-nlp:1.0.0-beta7")
    implementation("com.github.haifengl:smile-core:2.6.0")
    implementation("com.github.haifengl:smile-kotlin:2.6.0")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.21")
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