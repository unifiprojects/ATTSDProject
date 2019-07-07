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

	@Autowired
	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public List<User> getUsersByUsernameLike(String username) {
		return userRepository.findByUsernameLike(username);
	}

	public User insertNewUser(User user) {
		if (user == null)
			throw new IllegalArgumentException();

		user.setId(null);
		return userRepository.save(user);
	}

	public User updateUserById(Long id, User user) throws UserNotFoundException, IllegalArgumentException {
		if (id == null || user == null)
			throw new IllegalArgumentException();
		if (userRepository.findById(id) == null)
			throw new UserNotFoundException();

		user.setId(id);
		return userRepository.save(user);
	}

	public void deleteById(Long id) throws UserNotFoundException, IllegalArgumentException {
		if (id == null)
			throw new IllegalArgumentException();
		if (userRepository.findById(id) == null)
			throw new UserNotFoundException();

		userRepository.deleteById(id);
	}

	public User addUserToFollowedUsers(User user, User toAdd) {
		if (user == null || toAdd == null)
			throw new IllegalArgumentException();
		user.addFollowedUser(toAdd);
		toAdd.addFollowerUser(user);

		return userRepository.save(user);
	}

	public User addGameToGames(User user, Game game) {
		if (user == null || game == null)
			throw new IllegalArgumentException();
		user.addGame(game);
		game.addUser(user);

		return userRepository.save(user);
	}

}
