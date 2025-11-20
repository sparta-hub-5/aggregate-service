package org.spartahub.aggregateservice;

import org.bangbang.infrastructure.kafka.annotation.EnableKafkaClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableKafkaClients(basePackages = "org.spartahub.aggregateservice")
public class AggregateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregateServiceApplication.class, args);
    }

}
