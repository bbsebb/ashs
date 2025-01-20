package fr.hoenheimsports.backend_discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BackendDiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendDiscoveryServiceApplication.class, args);
	}

}
