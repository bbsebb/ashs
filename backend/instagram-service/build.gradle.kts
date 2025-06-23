plugins {
    java
    id("org.springframework.boot") version "3.5.1-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
}
val mapstructVersion = "1.6.3"
val mapstructSpringExtensionsVersion = "1.1.2"
group = "fr.hoenheimsports"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    // Logging
    implementation("com.github.loki4j:loki-logback-appender:1.6.0")
    // MapStruct Core
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    // MapStruct Spring Extensions
    implementation("org.mapstruct.extensions.spring:mapstruct-spring-annotations:$mapstructSpringExtensionsVersion")
    annotationProcessor("org.mapstruct.extensions.spring:mapstruct-spring-extensions:$mapstructSpringExtensionsVersion")

    testAnnotationProcessor("org.mapstruct.extensions.spring:mapstruct-spring-extensions:$mapstructSpringExtensionsVersion")
    testImplementation("org.mapstruct.extensions.spring:mapstruct-spring-test-extensions:$mapstructSpringExtensionsVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
