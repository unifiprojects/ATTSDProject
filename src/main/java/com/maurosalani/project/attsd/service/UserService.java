package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

@Service
public class UserService {

	private static final String USER_NOT_FOUND = "User not found";

	@Autowired
	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) throws UserNotFoundException {
		if (id == null)
			throw new IllegalArgumentException();

		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

	public User getUserByUsername(String username) throws UserNotFoundException {
		if (username == null)
			throw new IllegalArgumentException();

		return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

	public List<User> getUsersByUsernameLike(String username) {
		if (username == null)
			throw new IllegalArgumentException();

		return userRepository.findByUsernameLike(username);
	}

	public User insertNewUser(User user) {
		if (user == null)
			throw new IllegalArgumentException();

		user.setId(null);
		return userRepository.save(user);
	}

	public User updateUserById(Long id, User user) throws UserNotFoundException {
		if (id == null || user == null)
			throw new IllegalArgumentException();

		checkExistanceOfUser(id);

		user.setId(id);
		return userRepository.save(user);
	}

	public void deleteById(Long id) throws UserNotFoundException {
		if (id == null)
			throw new IllegalArgumentException();

		checkExistanceOfUser(id);

		userRepository.deleteById(id);
	}

	private void checkExistanceOfUser(Long id) throws UserNotFoundException {
		getUserById(id);
	}

	public User addFollowedUser(User user, User followedToAdd) {
		if (user == null || followedToAdd == null)
			throw new IllegalArgumentException();
		user.addFollowedUser(followedToAdd);
		followedToAdd.addFollowerUser(user);

		return userRepository.save(user);
	}

	public User addGame(User user, Game gameToAdd) {
		if (user == null || gameToAdd == null)
			throw new IllegalArgumentException();
		user.addGame(gameToAdd);
		gameToAdd.addUser(user);

		return userRepository.save(user);
	}

	public User getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException {
		return userRepository.findByUsernameAndPassword(username, password).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

}
