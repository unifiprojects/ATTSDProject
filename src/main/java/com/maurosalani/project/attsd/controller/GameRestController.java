package com.maurosalani.project.attsd.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.dto.GameDTO;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.service.GameService;

@RestController
@RequestMapping("/api/games")
public class GameRestController {

	@Autowired
	private GameService gameService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Game> getAllGames() {
		return gameService.getAllGames();
	}

	@GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Game getGameById(@PathVariable Long id) throws GameNotFoundException {
		return gameService.getGameById(id);
	}

	@GetMapping(path = "/name/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Game getGameByName(@PathVariable String name) throws GameNotFoundException {
		return gameService.getGameByName(name);
	}

	@GetMapping(path = "/namelike/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Game> getGamesByNameLike(@PathVariable String name) {
		return gameService.getGamesByNameLike(name);
	}

	@PostMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Game insertNewGame(@RequestBody GameDTO gameDto) {
		return gameService.insertNewGame(gameDto.getGame());
	}

	@PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Game updateGameById(@PathVariable Long id, @RequestBody GameDTO gameDto) throws GameNotFoundException {
		return gameService.updateGameById(id, gameDto.getGame());
	}

	@DeleteMapping(path = "/delete/{id}")
	public void deleteGame(@PathVariable Long id, HttpServletResponse response) throws GameNotFoundException {
		gameService.deleteById(id);
		response.setStatus(HttpStatus.NO_CONTENT.value());
	}
}
