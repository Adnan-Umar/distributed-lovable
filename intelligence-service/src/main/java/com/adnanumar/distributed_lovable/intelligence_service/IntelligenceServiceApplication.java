package com.adnanumar.distributed_lovable.intelligence_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class IntelligenceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelligenceServiceApplication.class, args);
	}

}
