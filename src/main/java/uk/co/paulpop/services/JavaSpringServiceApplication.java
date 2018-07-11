package uk.co.paulpop.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Core spring boot application for running the service
 */
@SpringBootApplication
public class JavaSpringServiceApplication {

    @PostConstruct
    protected void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaSpringServiceApplication.class, args);
    }
}
