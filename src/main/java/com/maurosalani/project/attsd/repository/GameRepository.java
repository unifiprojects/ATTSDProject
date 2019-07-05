package com.maurosalani.project.attsd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.maurosalani.project.attsd.model.Game;

@Repository
public class GameRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Game> findAll() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Game> findById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
