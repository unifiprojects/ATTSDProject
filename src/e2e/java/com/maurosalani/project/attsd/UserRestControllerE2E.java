package com.maurosalani.project.attsd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.model.User;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserRestControllerE2E {

	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	private static String baseUrl = "http://localhost:" + port;

	@Before
	public void setup() {
		RestAssured.port = port;
		try (
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/attsd_database?allowPublicKeyRetrieval=true&useSSL=false",
				"springuser", "springuser");
			Statement stmt = conn.createStatement();) {
			String strSelect = "delete from user";
			stmt.executeUpdate(strSelect);
			strSelect = "delete from game";
			stmt.executeUpdate(strSelect);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testHomePage() {
		Response response = 
				given().
				when().
				get(baseUrl + "/api/users");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	public void testNewUser_ShouldBeRetrievedCorrectly() {
		UserDTO userDto = new UserDTO(null, "username", "password");
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).when()
				.post("/api/users/new");
		User saved = responseNew.getBody().as(User.class);

		Response responseFind = 
				given().
				when().
				get("/api/users/id/" + saved.getId());

		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseFind.getBody().as(User.class)).isEqualTo(saved);
	}
	
	@Test
	public void testDeleteUser_ShouldNotBeAvailableAnymore() {
		UserDTO userDto = new UserDTO(null, "username", "password");
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
				post("/api/users/new");
		User saved = responseNew.getBody().as(User.class);
		
		Response responseDelete = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
					delete("/api/users/delete/" + saved.getId());
		
		assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		Response responseFind = 
				given().
				when().
				get("/api/users/id/" + saved.getId());

		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}
}
