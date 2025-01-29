
dependencies {
	implementation("org.springframework.cloud:spring-cloud-config-server:4.2.0") {
            exclude(group = "commons-logging", module = "commons-logging")
        }
    implementation("org.springframework:spring-jcl")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")


}

