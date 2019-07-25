package com.maurosalani.project.attsd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AttsdProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttsdProjectApplication.class, "spring.output.ansi.enabled=[always]");
	}
}
