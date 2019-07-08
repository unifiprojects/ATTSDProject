package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.repository.GameRepository;

@Service
public class GameService {

	private static final String GAME_NOT_FOUND = "Game not found";
	
	@Autowired
	private GameRepository gameRepository;

	public GameService(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	public List<Game> getAllGames() {
		return gameRepository.findAll();
	}

	public Game getGameById(Long id) throws GameNotFoundException {
		if (id == null)
			throw new IllegalArgumentException();
		
		return gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND));
	}

	public Game getGameByName(String name) throws GameNotFoundException {
		if (name == null)
			throw new IllegalArgumentException();
		
		return gameRepository.findByName(name).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND));
	}

	public List<Game> getGamesByNameLike(String name) {
		if (name == null)
			throw new IllegalArgumentException();
		
		return gameRepository.findByNameLike(name);
	}

	public Game insertNewGame(Game game) {
		if (game == null)
			throw new IllegalArgumentException();

		game.setId(null);
		return gameRepository.save(game);
	}

	public Game updateGameById(Long id, Game game) throws GameNotFoundException {
		if (id == null || game == null)
			throw new IllegalArgumentException();

		checkExistanceOfGame(id);

		game.setId(id);
		return gameRepository.save(game);
	}

	public void deleteById(Long id) throws GameNotFoundException {
		if (id == null)
			throw new IllegalArgumentException();

		checkExistanceOfGame(id);

		gameRepository.deleteById(id);
	}

	private void checkExistanceOfGame(Long id) throws GameNotFoundException {
		getGameById(id);
	}

}
