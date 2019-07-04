package com.maurosalani.project.attsd.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface GameRepository extends JpaRepository<Game, Long> {

	Game findByName(String string);

	Game findByNameLike(String string);

	@Query("select u.users from Game u where u.name = ?1")
	List<User> findUsersOfGameByName(String string);

}
