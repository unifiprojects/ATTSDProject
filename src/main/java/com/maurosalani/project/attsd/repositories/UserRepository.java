package com.maurosalani.project.attsd.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String string);
	
	
	List<Game> findGamesOfUserByUsername(String username);
}
