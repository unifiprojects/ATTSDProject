package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
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

	public User insertNewUser(User user) throws UsernameAlreadyExistingException {
		if (user == null)
			throw new IllegalArgumentException();
		checkIfUsernameAlreadyExists(user);
		
		user.setId(null);
		try {
			return userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("Username or password are invalid.");
		}
	}

	private void checkIfUsernameAlreadyExists(User user) throws UsernameAlreadyExistingException {
		if(userRepository.findByUsername(user.getUsername()).isPresent())
			throw new UsernameAlreadyExistingException("Username already existing.");
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

	public User addFollowedUser(User user, User followedToAdd) throws UserNotFoundException {
		if (user == null || followedToAdd == null)
			throw new IllegalArgumentException();
		
		checkExistanceOfUser(user.getId());
		checkExistanceOfUser(followedToAdd.getId());
		
		user.addFollowedUser(followedToAdd);
		followedToAdd.addFollowerUser(user);

		return userRepository.save(user);
	}

	public User addGame(User user, Game gameToAdd) throws UserNotFoundException {
		if (user == null || gameToAdd == null)
			throw new IllegalArgumentException();
		
		checkExistanceOfUser(user.getId());
		
		user.addGame(gameToAdd);
		gameToAdd.addUser(user);

		return userRepository.save(user);
	}

	public User getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException {
		if (username == null || password == null)
			throw new IllegalArgumentException();
		return userRepository.findByUsernameAndPassword(username, password)
				.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

}
