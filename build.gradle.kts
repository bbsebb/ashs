plugins {
    id("org.springframework.boot") version "3.4.1" apply false // Spring Boot plugin
    id("io.spring.dependency-management") version "1.1.7" apply false // Dependency Management plugin
    id("com.github.node-gradle.node") version "7.1.0" apply false // Node.js/Angular plugin
}

group = "fr.hoenheimsports"
version = "0.0.1-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral() // Use Maven Central repository for dependencies
    }
}