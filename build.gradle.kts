plugins {
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.dokka") version "1.6.21"
    id("com.jfrog.artifactory") version "4.28.3"
    `maven-publish`
    signing
}

group = "com.atlassian.kotlin.dsl"
version = "0.1"

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
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["release"])
    }
}

artifactory {
    publish {
        setContextUrl("https://packages.atlassian.com/")

        repository {
            setRepoKey("maven-central")
            setUsername(System.getenv("ARTIFACTORY_USERNAME"))
            setPassword(System.getenv("ARTIFACTORY_API_KEY"))
        }
        defaults {
            publications("release")
            setPublishIvy(false)
        }
    }
}

repositories {
    mavenCentral()
}
