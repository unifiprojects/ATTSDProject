package com.maurosalani.project.attsd.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.maurosalani.project.attsd.model.User;

@Repository
public class UserRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<User> findAllUsers() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}
