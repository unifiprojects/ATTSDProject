package com.maurosalani.project.attsd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.GameDTO;
import com.maurosalani.project.attsd.dto.UpdateAddFollowedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateAddGameLikedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdatePasswordUserFormDTO;
import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.model.Game;
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
			String strDelete = "delete from followers_relation";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from user_game_relation";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from user";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from game";
			stmt.executeUpdate(strDelete);
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
					body(userDto).
				when().
					post("/api/users/new");
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
	
	@Test
	public void testChangePassword_ShouldBeUpdatedWithSuccess() {
		UserDTO userDto = new UserDTO(null, "username", "password");
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
					post("/api/users/new");
		User saved = responseNew.getBody().as(User.class);
		
		UpdatePasswordUserFormDTO form = new UpdatePasswordUserFormDTO();
		form.setCredentials(new CredentialsDTO("username", "password"));
		form.setNewPassword("newPassword");
		Response responseChangePassword = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					patch("/api/users/update/password/" + saved.getId());
		
		assertThat(responseChangePassword.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		
		Response responseFind = 
				given().
				when().
					get("/api/users/id/" + saved.getId());
		
		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseFind.getBody().as(User.class).getPassword()).isEqualTo("newPassword");
	}
	
	@Test
	public void testAddFollowedUser_ShouldBeUpdatedWithSuccess() {
		UserDTO userDto = new UserDTO(null, "username", "password");
		UpdateAddFollowedUserFormDTO form = new UpdateAddFollowedUserFormDTO();
		form.setCredentials(new CredentialsDTO("username", "password"));
		UserDTO followedToAddDTO = new UserDTO(null, "followed", "followed_pwd");
		
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
					post("/api/users/new");
		User saved = responseNew.getBody().as(User.class);
		 
		Response responseNewFollowed = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(followedToAddDTO).
				when().
					post("/api/users/new");
		User savedFollowed = responseNewFollowed.getBody().as(User.class);
		form.setFollowedToAdd(savedFollowed);
		
		Response responseAddFollowed = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					patch("/api/users/update/addFollowedUser/" + saved.getId());
		
		assertThat(responseAddFollowed.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		
		Response responseFind = 
				given().
				when().
					get("/api/users/id/" + saved.getId());
		
		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		User updated = responseFind.getBody().as(User.class);
		assertThat(updated.getFollowedUsers().get(0).getUsername()).isEqualTo("followed");
	}
	
	@Test
	public void testAddGameLiked_ShouldBeUpdatedWithSuccess() {
		UserDTO userDto = new UserDTO(null, "username", "password");
		UpdateAddGameLikedUserFormDTO form = new UpdateAddGameLikedUserFormDTO();
		form.setCredentials(new CredentialsDTO("username", "password"));
		GameDTO gameLikedDTO = new GameDTO(null, "gameLiked", "description", new Date(1000));
		
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
					post("/api/users/new");
		User saved = responseNew.getBody().as(User.class);
		
		Response responseNewGame = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(gameLikedDTO).
				when().
					post("/api/games/new");
		Game savedGame = responseNewGame.getBody().as(Game.class);
		form.setGameLiked(savedGame);
		
		Response responseAddGame = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					patch("/api/users/update/addGame/" + saved.getId());
		
		assertThat(responseAddGame.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		
		Response responseFind = 
				given().
				when().
					get("/api/users/id/" + saved.getId());
		
		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		User updated = responseFind.getBody().as(User.class);
		assertThat(updated.getGames().get(0).getName()).isEqualTo("gameLiked");
	}
}
