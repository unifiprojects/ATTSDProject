package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.model.Game;

@Service
public class GameService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Game> getGamesByNameLike(String content) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Game getGamesByName(String name) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
