plugins {
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.dokka") version "1.6.21"
}

group = "com.github.detouched"
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
        kotlinOptions.jvmTarget = "1.8"
    }

    // This task is added by Gradle when we use java.withJavadocJar()
    named<Jar>("javadocJar") {
        from(dokkaJavadoc)
    }

    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}
