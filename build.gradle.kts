plugins {
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.dokka") version "1.6.21"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `maven-publish`
    signing
}

group = "io.github.detouched"
version = "0.1-SNAPSHOT"

dependencies {
    val assertkVersion = "0.24"
    val junitVersion = "5.8.2"
    testImplementation("com.willowtreeapps.assertk", "assertk-jvm", assertkVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", junitVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            allWarningsAsErrors = true
        }
    }

    // This task is added by Gradle when we use java.withJavadocJar()
    named<Jar>("javadocJar") {
        from(dokkaJavadoc)
    }

    test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(project.components["java"])
                pom {
                    packaging = "jar"
                    name.set(project.name)
                    description.set("URL building Kotlin DSL")
                    url.set("https://github.com/detouched/urlme")
                    scm {
                        connection.set("git@github.com:detouched/urlme.git")
                        url.set("https://github.com/detouched/urlme.git")
                    }
                    developers {
                        developer {
                            id.set("detouched")
                            name.set("Daniil Penkin")
                            email.set("dpenkin@gmail.com")
                        }
                    }
                    licenses {
                        license {
                            name.set("BSD 2-Clause License")
                            url.set("https://github.com/detouched/urlme/blob/main/LICENSE")
                        }
                    }
                }
            }
        }
    }

    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["release"])
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

repositories {
    mavenCentral()
}
