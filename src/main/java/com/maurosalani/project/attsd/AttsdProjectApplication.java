package com.maurosalani.project.attsd;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.matteomauro.notification_server.UserSessionHandler;
import com.matteomauro.notification_server.WebSocketServer;
import com.matteomauro.notification_server.repository.RedisRepository;

@SpringBootApplication
@EnableWebSocket
@ComponentScan("com.matteomauro.notification_server")
public class AttsdProjectApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(AttsdProjectApplication.class, args);
	}

	@Bean
	public WebSocketServer webSocketServer() {
		return new WebSocketServer();
	}
	
	@Bean
	public UserSessionHandler userSessionHandler() {
		return new UserSessionHandler();
	}
	
	@Bean
	public RedisRepository redisRepository() {
		return new RedisRepository();
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Override
	public void run(String... args) throws Exception {
		Stream<String> stream = Arrays.stream(applicationContext.getBeanDefinitionNames());
		System.out.println("-------------------------------------------------------");
//		stream.filter(name -> name.contains("WebSocketServer")).forEach(name -> System.out.println(name));
		stream.forEach(name -> System.out.println(name));
		System.out.println("-------------------------------------------------------");
	}
}
