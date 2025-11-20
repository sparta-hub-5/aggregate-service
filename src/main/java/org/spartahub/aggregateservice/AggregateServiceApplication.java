package org.spartahub.aggregateservice;

import org.bangbang.infrastructure.kafka.annotation.EnableKafkaClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafkaClients(basePackages = "org.spartahub.aggregateservice")
public class AggregateServiceApplication {

    /**
     * Starts the Spring Boot application, bootstrapping the Spring context and initializing Kafka clients for the package.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(AggregateServiceApplication.class, args);
    }

}