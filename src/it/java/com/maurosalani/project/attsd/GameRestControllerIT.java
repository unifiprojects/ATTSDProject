package com.maurosalani.project.attsd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.dto.GameDTO;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.repository.GameRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mysql")
public class GameRestControllerIT {

	@Autowired
	private GameRepository gameRepository;
	
	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		gameRepository.deleteAll();
		gameRepository.flush();
	}
	
	@Test
	public void testNewGame() throws Exception {
		GameDTO gameDto = new GameDTO(null, "name", "description", new Date(1000));
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(gameDto).
				when().
					post("/api/games/new");
		Game saved = response.getBody().as(Game.class);
		
		assertThat(gameRepository.findById(saved.getId()).get()).isEqualTo(saved);
	}
	
	@Test
	public void testUpdate_WithExistingGame() throws Exception {
		Game toReplace = gameRepository.save(new Game(null, "toReplace", "toReplace", new Date(1000)));
		GameDTO gameReplacement = new GameDTO(null, "new_name", "new_description", new Date(999));
		
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(gameReplacement).
				when().
					put("/api/games/update/" + toReplace.getId());
		Game updated = response.getBody().as(Game.class);

		Game assertion = gameRepository.findById(updated.getId()).get();
		assertThat(assertion).isEqualTo(updated);
	}
}
