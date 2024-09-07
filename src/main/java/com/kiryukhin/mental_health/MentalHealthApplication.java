package com.kiryukhin.mental_health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MentalHealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentalHealthApplication.class, args);
	}

}
