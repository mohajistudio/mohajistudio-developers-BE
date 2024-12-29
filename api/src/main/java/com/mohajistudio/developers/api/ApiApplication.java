package com.mohajistudio.developers.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.mohajistudio.developers.api", "com.mohajistudio.developers.authentication", "com.mohajistudio.developers.database", "com.mohajistudio.developers.common", "com.mohajistudio.developers.infra"})
@EnableJpaRepositories(basePackages = "com.mohajistudio.developers.database.repository")
@EntityScan(basePackages = "com.mohajistudio.developers.database.entity")
@EnableJpaAuditing
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
