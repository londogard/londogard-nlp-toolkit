import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    signing
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"
    id("org.jetbrains.dokka") version "1.5.0"
}

group = "com.londogard"
version = "1.1.0-BETA"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

val kluentVersion: String by project

dependencies {
    implementation("com.github.rholder:snowball-stemmer:1.3.0.581.1")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.jetbrains.kotlinx:dataframe:0.8.0-dev-299-0.10.0.201")

    // EJML
    implementation("org.ejml:ejml-simple:0.41")
    implementation("org.ejml:ejml-kotlin:0.41")

    // Multik
    implementation("org.jetbrains.kotlinx:multik-api:0.0.1")
    implementation("org.jetbrains.kotlinx:multik-default:0.0.1")
    implementation("org.jetbrains.kotlinx:multik-jvm:0.0.1")

    // DJL
    implementation("ai.djl:api:0.14.0")
    implementation("ai.djl.pytorch:pytorch-engine:0.12.0")
    implementation("ai.djl.pytorch:pytorch-native-auto:1.9.1")
    implementation("ai.djl.sentencepiece:sentencepiece:0.14.0")


    // Logging
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.0")

    // Standard Libary
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Testing
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = listOf("-Xmx2g")
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    val key = System.getenv("SIGNING_KEY")
    val password = System.getenv("SIGNING_PASSWORD")
    val publishing: PublishingExtension by project

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("OSSRH_USER")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
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
        val main by creating(MavenPublication::class) {
            from(components["java"])

            pom {
                name.set("londogard-nlp-toolkit")
                description.set("londogard-nlp-toolkit is a library that provides utilities when working with natural language processing such as word/subword/sentence embeddings, word-frequencies, stopwords, stemming, and much more.")
                url.set("https://github.com/londogard/londogard-nlp-toolkit/")
                licenses {
                    license {
                        name.set("GPL-3.0 License")
                        url.set("https://github.com/londogard/londogard-nlp-toolkit/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("londogard")
                        name.set("Hampus Londögård")
                        email.set("hampus.londogard@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:londogard/londogard-nlp-toolkit.git")
                    developerConnection.set("scm:git:git@github.com:londogard/londogard-nlp-toolkit.git")
                    url.set("https://github.com/londogard/londogard-nlp-toolkit/")
                }
            }
        }
        register<MavenPublication>("gpr") {
            from(components["java"])

            pom {
                name.set("londogard-nlp-toolkit")
                description.set("londogard-nlp-toolkit is a library that provides utilities when working with natural language processing such as word/subword/sentence embeddings, word-frequencies, stopwords, stemming, and much more.")
                url.set("https://github.com/londogard/londogard-nlp-toolkit/")
                licenses {
                    license {
                        name.set("GPL-3.0 License")
                        url.set("https://github.com/londogard/londogard-nlp-toolkit/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("londogard")
                        name.set("Hampus Londögård")
                        email.set("hampus.londogard@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:londogard/londogard-nlp-toolkit.git")
                    developerConnection.set("scm:git:git@github.com:londogard/londogard-nlp-toolkit.git")
                    url.set("https://github.com/londogard/londogard-nlp-toolkit/")
                }
            }
        }
    }
}