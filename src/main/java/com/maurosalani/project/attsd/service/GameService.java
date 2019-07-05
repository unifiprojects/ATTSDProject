package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.repository.GameRepository;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;

	public GameService(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	public List<Game> getAllGames() {
		return gameRepository.findAll();
	}

	public Game getGameById(Long id) {
		return gameRepository.findById(id).orElse(null);
	}

	public Game insertNewGame(Game game) {
		game.setId(null);
		return gameRepository.save(game);
	}

}
