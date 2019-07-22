package com.maurosalani.project.attsd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

public interface GameRepository extends JpaRepository<Game, Long> {

	Optional<Game> findByName(String string);

	List<Game> findByNameLike(String string);

	@Query("select u.users from Game u where u.name = ?1")
	List<User> findUsersOfGameByName(String string);

	@Query("select u from Game u order by u.releaseDate desc")
	List<Game> findFirstNOrderByReleaseDate(Pageable pageable);	

}
