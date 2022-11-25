package com.example.foodserviceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource("classpath:/env.yml")
public class FoodServiceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodServiceAppApplication.class, args);
	}

}
