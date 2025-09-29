package com.sp.inventory_ms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryMsApplication.class, args);
	}
	
	@Bean
	CommandLineRunner runner(InventoryRepository repository) {
		return args -> {
			// Corresponds to product IDs from ProductService
			repository.save(new Inventory(1L, 100)); // Laptop
			repository.save(new Inventory(2L, 500)); // Mouse
			repository.save(new Inventory(3L, 250)); // Keyboard
		};
	}
}
