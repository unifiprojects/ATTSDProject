package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.dto.Credentials;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.Game;
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

	public User updateUserById(Long id, User user) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User changePassword(User user, String password) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteUserById(Long id) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User verifyLogin(Credentials credentials) throws LoginFailedException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User addFollowedUser(User user, User followedToAdd) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User addGame(User user, Game gameLiked) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
