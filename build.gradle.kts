plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    `maven-publish`
    signing
}

group = "com.atlassian.kotlin.dsl"
version = "0.1"

dependencies {
    val assertkVersion = "0.25"
    val junitVersion = "5.9.1"
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
        repositories {
            maven {
                setUrl("https://packages.atlassian.com/maven-central")
                credentials {
                    username = System.getenv("ARTIFACTORY_USERNAME")
                    password = System.getenv("ARTIFACTORY_API_KEY")
                }
            }
        }

        publications {
            create<MavenPublication>("release") {
                from(project.components["java"])
                pom {
                    packaging = "jar"
                    name.set(project.name)
                    description.set("URL building Kotlin DSL")
                    url.set("https://github.com/atlassian-labs/kotlin-url-dsl")
                    scm {
                        connection.set("git@github.com:atlassian-labs/kotlin-url-dsl.git")
                        url.set("https://github.com/atlassian-labs/kotlin-url-dsl.git")
                    }
                    developers {
                        developer {
                            id.set("dpenkin")
                            name.set("Daniil Penkin")
                            email.set("dpenkin@atlassian.com")
                        }
                    }
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                            distribution.set("repo")
                        }
                    }
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_PASSWORD"),
        )
        sign(publishing.publications["release"])
    }
}

repositories {
    mavenCentral()
}
