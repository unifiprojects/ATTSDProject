package com.maurosalani.project.attsd.service;

import java.util.List;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.model.Game;

public class GameService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Game> getAllGames() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Game getGameById(Long id) throws GameNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Game getGameByName(String name) throws GameNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<Game> getGamesByNameLike(String name) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Game insertNewGame(Game game) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Game updateGameById(Long id, Game game) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
