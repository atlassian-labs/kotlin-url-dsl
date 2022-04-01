plugins {
    kotlin("jvm") version "1.6.10"
}

group = "com.github.detouched"
version = "0.1-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
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
