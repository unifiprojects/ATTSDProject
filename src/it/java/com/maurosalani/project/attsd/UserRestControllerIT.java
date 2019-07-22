package com.maurosalani.project.attsd;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.repository.UserRepository;

import io.restassured.RestAssured;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserRestControllerIT {

	@Autowired
	private UserRepository userRepository;
	
	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		userRepository.deleteAll();
		userRepository.flush();
	}

}
