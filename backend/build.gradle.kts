plugins {
  id("java")
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

  tasks.withType<Test> {
      useJUnitPlatform()
  }
}

