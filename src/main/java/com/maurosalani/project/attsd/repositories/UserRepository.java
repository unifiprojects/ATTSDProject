package com.maurosalani.project.attsd.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String string);

	@Query("select u.games from User u where u.username = ?1")
	List<Game> findGamesOfUserByUsername(String username);

	List<User> findFollowedOfUserByUsername(String string);
}
