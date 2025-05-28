package fr.hoenheimsports.contactservice;

import org.springframework.boot.SpringApplication;

public class TestContactServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ContactServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
