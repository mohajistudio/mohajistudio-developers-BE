package com.mohajistudio.developers.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication(scanBasePackages = {"com.mohajistudio.developers.admin", "com.mohajistudio.developers.authentication", "com.mohajistudio.developers.database", "com.mohajistudio.developers.common"})
@EnableJpaRepositories(basePackages = "com.mohajistudio.developers.database.repository")
@EntityScan(basePackages = "com.mohajistudio.developers.database.entity")
@EnableJpaAuditing
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
