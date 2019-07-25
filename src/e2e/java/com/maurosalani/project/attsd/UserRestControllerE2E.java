package com.maurosalani.project.attsd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserRestControllerE2E {

	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	private static String baseUrl = "http://localhost:" + port;

	@Before
	public void setup() {
		RestAssured.port = port;
	}
	
	@Test
	public void testHomePage() {
		Response response =
				given().
				when().
					get(baseUrl + "/api/users");
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
	}

}
