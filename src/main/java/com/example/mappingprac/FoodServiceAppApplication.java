package com.example.mappingprac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FoodServiceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodServiceAppApplication.class, args);
	}

}
