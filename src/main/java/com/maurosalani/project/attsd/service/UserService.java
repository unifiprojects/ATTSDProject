package com.maurosalani.project.attsd.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;
import com.maurosalani.project.attsd.repository.UserRepository;

@Service
public class UserService {

	private static final String GAME_NOT_FOUND = "Game not found";

	private static final String USER_NOT_FOUND = "User not found";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	public UserService(UserRepository userRepository, GameRepository gameRepository) {
		this.userRepository = userRepository;
		this.gameRepository = gameRepository;
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
		String usernameValid = '%' + username.trim() + '%';
		return userRepository.findByUsernameLike(usernameValid);
	}

	public User insertNewUser(User user) throws UsernameAlreadyExistingException, PasswordRequiredException {
		if (user == null)
			throw new IllegalArgumentException();

		checkIfUsernameAlreadyExists(user);
		checkPasswordIsCorrect(user.getPassword());

		user.setId(null);
		try {
			return userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("Username or password are invalid.");
		}
	}

	private void checkIfUsernameAlreadyExists(User user) throws UsernameAlreadyExistingException {
		if (userRepository.findByUsername(user.getUsername()).isPresent())
			throw new UsernameAlreadyExistingException("Username already existing.");
	}

	public User updateUserById(Long id, User user) throws UserNotFoundException, PasswordRequiredException {
		if (id == null || user == null)
			throw new IllegalArgumentException();

		checkExistanceOfUser(id);
		checkPasswordIsCorrect(user.getPassword());

		user.setId(id);
		return userRepository.save(user);
	}

	public void deleteById(Long id) throws UserNotFoundException {
		if (id == null)
			throw new IllegalArgumentException();

		checkExistanceOfUser(id);

		userRepository.deleteById(id);
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

	public User addGame(User user, Game gameToAdd) throws UserNotFoundException, GameNotFoundException {
		if (user == null || gameToAdd == null)
			throw new IllegalArgumentException();

		checkExistanceOfUser(user.getId());
		checkExistanceOfGame(gameToAdd.getId());

		user.addGame(gameToAdd);
		gameToAdd.addUser(user);

		return userRepository.save(user);
	}

	public User changePassword(User user, String newPassword) throws UserNotFoundException, PasswordRequiredException {
		if (user == null)
			throw new IllegalArgumentException();

		checkPasswordIsCorrect(newPassword);
		checkExistanceOfUser(user.getId());
		user.setPassword(newPassword);
		return userRepository.save(user);
	}

	private void checkPasswordIsCorrect(String password) throws PasswordRequiredException {
		if (password == null || StringUtils.isWhitespace(password))
			throw new PasswordRequiredException();
	}

	public User getUserByUsernameAndPassword(String username, String password) throws UserNotFoundException {
		if (username == null || password == null)
			throw new IllegalArgumentException();
		return userRepository.findByUsernameAndPassword(username, password)
				.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

	private void checkExistanceOfUser(Long id) throws UserNotFoundException {
		userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
	}

	private void checkExistanceOfGame(Long id) throws GameNotFoundException {
		gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND));
	}

	public User verifyLogin(CredentialsDTO credentialsDTO) throws LoginFailedException {
		return userRepository.findByUsernameAndPassword(credentialsDTO.getUsername(), credentialsDTO.getPassword())
				.orElseThrow(LoginFailedException::new);
	}
}
