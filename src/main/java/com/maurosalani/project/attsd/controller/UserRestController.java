package com.maurosalani.project.attsd.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.UpdateAddFollowedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateAddGameLikedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdatePasswordUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateUserFormDTO;
import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.exception.BadRequestException;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UserService userService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping(path = "/id/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserById(@PathVariable Long id) throws UserNotFoundException {
		return userService.getUserById(id);
	}

	@GetMapping(path = "/username/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserByUsername(@PathVariable String username) throws UserNotFoundException {
		return userService.getUserByUsername(username);
	}

	@GetMapping(path = "/usernamelike/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<User> getUsersByUsernameLike(@PathVariable String username) {
		return userService.getUsersByUsernameLike(username);
	}

	@PostMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User insertNewUser(@RequestBody UserDTO userDto)
			throws UsernameAlreadyExistingException, PasswordRequiredException {
		return userService.insertNewUser(userDto.getUser());
	}

	@PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User updateUser(@PathVariable Long id, @RequestBody UpdateUserFormDTO form)
			throws UserNotFoundException, LoginFailedException, BadRequestException, PasswordRequiredException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.updateUserById(id, form.getUserToUpdate());
	}

	@PatchMapping(path = "/update/password/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User updatePasswordOfUser(@PathVariable Long id, @RequestBody UpdatePasswordUserFormDTO form)
			throws UserNotFoundException, LoginFailedException, BadRequestException, PasswordRequiredException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.changePassword(userService.getUserById(id), form.getNewPassword());
	}

	@PatchMapping(path = "/update/addFollowedUser/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User addFollowedUser(@PathVariable Long id, @RequestBody UpdateAddFollowedUserFormDTO form)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.addFollowedUser(userService.getUserById(id), form.getFollowedToAdd());
	}

	@PatchMapping(path = "/update/addGame/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User addGameLiked(@PathVariable Long id, @RequestBody UpdateAddGameLikedUserFormDTO form)
			throws UserNotFoundException, LoginFailedException, BadRequestException, GameNotFoundException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.addGame(userService.getUserById(id), form.getGameLiked());
	}

	@DeleteMapping(path = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void deleteUser(@PathVariable Long id, @RequestBody CredentialsDTO credentialsDTO, HttpServletResponse response)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(credentialsDTO);
		checkRequestCorrectness(id, userLogged);
		userService.deleteById(id);
		response.setStatus(HttpStatus.NO_CONTENT.value());
	}

	private void checkRequestCorrectness(Long id, User userLogged) throws BadRequestException {
		if (!userLogged.getId().equals(id))
			throw new BadRequestException();
	}

}
