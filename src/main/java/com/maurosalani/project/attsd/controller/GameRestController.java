package com.maurosalani.project.attsd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.exception.BadRequestException;
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
	
	
	
	
	
	
	
	@GetMapping(path = "/id")
	public Game getGameByIdWithNoId() throws BadRequestException {
		throw new BadRequestException();
	}

	@GetMapping(path = "/name")
	public Game getGameByNameWithNoName() throws BadRequestException {
		throw new BadRequestException();
	}

	@GetMapping(path = "/namelike")
	public List<Game> getGamesByNameLikeWithNoName() throws BadRequestException {
		throw new BadRequestException();
	}
}
