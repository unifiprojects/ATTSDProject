package com.maurosalani.project.attsd.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maurosalani.project.attsd.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

	Game findByName(String string);

}
