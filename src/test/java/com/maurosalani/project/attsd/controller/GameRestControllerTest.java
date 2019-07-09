package com.maurosalani.project.attsd.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception_handler.GlobalExceptionHandler;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.service.GameService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(MockitoJUnitRunner.class)
public class GameRestControllerTest {

	@InjectMocks
	private GameRestController gameRestController;

	@Mock
	private GameService gameService;

	@Before
	public void setup() {
		HandlerExceptionResolver handlerExceptionResolver = initGlobalExceptionHandlerResolvers();

		RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(gameRestController)
				.setHandlerExceptionResolvers(handlerExceptionResolver));
	}

	/**
	 * Necessary to register the exception handler for these unit tests
	 * 
	 * @return
	 */
	private HandlerExceptionResolver initGlobalExceptionHandlerResolvers() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("exceptionHandler", GlobalExceptionHandler.class);

		WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		return webMvcConfigurationSupport.handlerExceptionResolver();
	}

	@Test
	public void testFindAllGamesWithEmptyDatabase() {
		when(gameService.getAllGames()).thenReturn(Collections.emptyList());

		given().
		when().
			get("/api/games").
		then().
			statusCode(200).
			assertThat().
				body(is(equalTo("[]")));
	}

	@Test
	public void testFindAllGamesWithExistingGames() {
		Game game1 = new Game(1L, "game1", "description1", new Date(1000));
		Game game2 = new Game(2L, "game2", "description2", new Date(1000));
		when(gameService.getAllGames()).thenReturn(asList(game1, game2));

		given().
		when().
			get("/api/games").
		then().
			statusCode(200).
			contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
			assertThat().
			body("id[0]", equalTo(1), 
				"name[0]", equalTo("game1"), 
				"description[0]", equalTo("description1"),
				"releaseDate[0]", equalTo(1000),
				"id[1]", equalTo(2), 
				"name[1]", equalTo("game2"), 
				"description[1]", equalTo("description2"),
				"releaseDate[1]", equalTo(1000));

	}
	
	@Test
	public void testFindGameByIdWhenNotFound() throws GameNotFoundException {
		when(gameService.getGameById(anyLong())).thenThrow(GameNotFoundException.class);

		given().
		when().
			get("/api/games/id/1").
		then().	
			statusCode(404).
			statusLine(containsString("Game Not Found"));
	}
	
	@Test
	public void testFindGameByIdWithExistingGame() throws GameNotFoundException {
		when(gameService.getGameById(anyLong())).thenReturn(new Game(1L, "name", "description", new Date(1000)));
		
		given().
		when().
			get("/api/games/id/1").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"name", equalTo("name"), 
				"description", equalTo("description"),
				"releaseDate", equalTo(1000));
	}
	
	@Test
	public void testFindGameByNameWhenNotFound() throws GameNotFoundException {
		when(gameService.getGameByName(anyString())).thenThrow(GameNotFoundException.class);
		
		given().
		when().
			get("/api/games/name/testName").
		then().	
			statusCode(404).
			statusLine(containsString("Game Not Found"));
	}
	
	@Test
	public void testFindGameByNameWithExistingGame() throws GameNotFoundException {
		when(gameService.getGameByName(anyString())).thenReturn(new Game(1L, "name", "description", new Date(1000)));
		
		given().
		when().
			get("/api/games/name/testName").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"name", equalTo("name"), 
				"description", equalTo("description"),
				"releaseDate", equalTo(1000));
	}
}
