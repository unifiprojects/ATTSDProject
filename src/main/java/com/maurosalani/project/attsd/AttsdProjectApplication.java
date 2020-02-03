package com.maurosalani.project.attsd;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket

//necessary to load all beans from the dependency and from this app
@ComponentScan(basePackages = { "com.maurosalani.push_notification", "com.maurosalani.project.attsd" })
public class AttsdProjectApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(AttsdProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Stream<String> stream = Arrays.stream(applicationContext.getBeanDefinitionNames());
		System.out.println("-------------------------------------------------------");
		stream.filter(name -> !name.contains("org")).forEach(name -> System.out.println(name));
		System.out.println("-------------------------------------------------------");
	}
}
