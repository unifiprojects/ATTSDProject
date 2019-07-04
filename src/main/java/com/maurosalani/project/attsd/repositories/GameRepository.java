package com.maurosalani.project.attsd.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface GameRepository extends JpaRepository<Game, Long> {

	Game findByName(String string);

	Game findByNameLike(String string);

	List<User> findUsersOfGameByName(String string);

}
