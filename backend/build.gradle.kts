plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(23)
        }
    }

    extra["springCloudVersion"] = "2024.0.0"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {

 //Cache
        implementation("org.springframework.boot:spring-boot-starter-cache")
        implementation("com.github.ben-manes.caffeine:caffeine")
 // Obs
   implementation("org.springframework.boot:spring-boot-starter-actuator")
   implementation("io.micrometer:micrometer-tracing-bridge-brave")
   implementation("io.zipkin.reporter2:zipkin-reporter-brave")
   implementation("com.github.loki4j:loki-logback-appender:1.6.0")
   runtimeOnly("io.micrometer:micrometer-registry-prometheus")

 //dev
        compileOnly("org.projectlombok:lombok")
        developmentOnly("org.springframework.boot:spring-boot-devtools")
        annotationProcessor("org.projectlombok:lombok")
 //test
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}


