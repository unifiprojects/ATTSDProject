package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

@Service
public class UserService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public User getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User insertNewUser(User user) throws UsernameAlreadyExistingException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<User> getUsersByUsernameLike(String content) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User addFollowedUser(User user, User toAdd) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public User addGame(User user, Game toAdd) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
