package com.maurosalani.project.attsd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.maurosalani.project.attsd.model.User;

@Repository
public class UserRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<User> findAll() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<User> findById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<User> findByUsername(String username) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<User> findByUsernameLike(String username) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User save(User user) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
