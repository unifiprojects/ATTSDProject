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

import com.maurosalani.project.attsd.dto.GameDTO;
import com.maurosalani.project.attsd.model.Game;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GameRestControllerE2E {

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
	public void testBaseUrl() {
		Response response = 
				given().
				when().
					get(baseUrl);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@Test
	public void testGetAllUsers() {
		Game saved1 = insertGameInDatabase(new GameDTO(null, "name1", "description", new Date(1000)));
		Game saved2 = insertGameInDatabase(new GameDTO(null, "name2", "description", new Date(1000)));
		
		Game[] games =  
				given().
				when().
					get("/api/games").
				then().
					statusCode(200).
					extract().
					as(Game[].class);
		assertThat(games[0]).matches(game -> game.equals(saved1) || game.equals(saved2));
		assertThat(games[1]).matches(game -> game.equals(saved1) || game.equals(saved2));

	}
	
	@Test
	public void testNewGame_ShouldBeRetrievedCorrectly() {
		Game saved = insertGameInDatabase(new GameDTO(null, "name", "description", new Date(1000)));

		Response responseFind = 
				given().
				when().
					get("/api/games/id/" + saved.getId());

		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseFind.getBody().as(Game.class)).isEqualTo(saved);
	}
	
	@Test
	public void testDeleteGame_ShouldNotBeAvailableAnymore() {
		GameDTO gameDto = new GameDTO(null, "name", "description", new Date(1000));
		Game saved = insertGameInDatabase(gameDto);
		
		Response responseDelete = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(gameDto).
				when().
					delete("/api/games/delete/" + saved.getId());
		
		assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		
		Response responseFind = 
				given().
				when().
					get("/api/games/id/" + saved.getId());

		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}
	
	@Test
	public void testUpdateGame_ShouldBeUpdatedWithSuccess() {
		Game saved = insertGameInDatabase(new GameDTO(null, "name", "description", new Date(1000)));
		
		GameDTO replacement = new GameDTO(null, "name", "new_description", new Date(1000));
		Response responseUpdateGame = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(replacement).
				when().
					put("/api/games/update/" + saved.getId());
		
		assertThat(responseUpdateGame.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		
		Response responseFind = 
				given().
				when().
					get("/api/games/id/" + saved.getId());
		
		assertThat(responseFind.getStatusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(responseFind.getBody().as(Game.class).getDescription()).isEqualTo("new_description");
	}
	
	private Game insertGameInDatabase(GameDTO gameDto) {
		Response responseNew = 
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(gameDto).
				when().
				post("/api/games/new");
		Game saved = responseNew.getBody().as(Game.class);
		return saved;
	}
}