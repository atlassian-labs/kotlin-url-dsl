plugins {
    kotlin("jvm") version "1.6.10"
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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}
