package com.cultivaplus.AEP_6S;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.cultivaplus.AEP_6S", "service", "controller"})
@EnableMongoRepositories(basePackages = "repository")
public class Aep6SApplication {

    public static void main(String[] args) {
        SpringApplication.run(Aep6SApplication.class, args);
    }

}