package com.maurosalani.project.attsd.service;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.User;

@Service
public class UserService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public User getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User getUserByUsername(String username) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User insertNewUser(User user) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
