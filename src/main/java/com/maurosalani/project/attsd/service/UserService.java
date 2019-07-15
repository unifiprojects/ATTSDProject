package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.BadRequestException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.User;

@Service
public class UserService {
	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<User> getAllUsers() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User getUserById(Long id) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<User> getUsersByUsernameLike(String username) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User insertNewUser(User user) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User updateUserById(Long id, User user) throws UserNotFoundException, BadRequestException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteUserById(Long id) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public boolean verifyLogin(String username, String password) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
