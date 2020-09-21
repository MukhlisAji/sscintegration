package com.mii.sscintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SscintegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SscintegrationApplication.class, args);
		/*
		try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(ConfigFile.class, ConfigurationValue.class);) {
			ConfigurationValue configurationValue = context.getBean(ConfigurationValue.class);
	          //logger.info("This is dbProperties: " + dbProperties.toString());
	        }*/
	}
}
