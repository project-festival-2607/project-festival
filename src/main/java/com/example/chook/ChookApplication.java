package com.example.chook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChookApplication.class, args);
	}

}
