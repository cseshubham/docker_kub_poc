package com.sp.product_ms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProductMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductMsApplication.class, args);
	}
	
	@Bean
	CommandLineRunner runner(ProductRepository repository) {
		return args -> {
			repository.save(new Product("Laptop", 1200.00));
			repository.save(new Product("Mouse", 25.00));
			repository.save(new Product("Keyboard", 75.00));
		};
	}

}
