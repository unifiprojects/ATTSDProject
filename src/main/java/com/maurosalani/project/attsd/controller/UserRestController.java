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

import com.maurosalani.project.attsd.dto.Credentials;
import com.maurosalani.project.attsd.dto.UpdateAddFollowedUserForm;
import com.maurosalani.project.attsd.dto.UpdatePasswordUserForm;
import com.maurosalani.project.attsd.dto.UpdateUserForm;
import com.maurosalani.project.attsd.exception.BadRequestException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
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
	public User insertNewUser(@RequestBody User user) {
		return userService.insertNewUser(user);
	}

	@PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User updateUser(@PathVariable Long id, @RequestBody UpdateUserForm form)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.updateUserById(id, form.getUserToUpdate());
	}

	@PatchMapping(path = "/update/password/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User updatePasswordOfUser(@PathVariable Long id, @RequestBody UpdatePasswordUserForm form)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.updatePasswordById(id, form.getNewPassword());
	}

	@PatchMapping(path = "/update/addFollowedUser/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User addFollowedUser(@PathVariable Long id, @RequestBody UpdateAddFollowedUserForm form)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(form.getCredentials());
		checkRequestCorrectness(id, userLogged);
		return userService.updateAddFollowedUserById(id, form.getFollowedToAdd());
	}

	@DeleteMapping(path = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void deleteUser(@PathVariable Long id, @RequestBody Credentials credentials, HttpServletResponse response)
			throws UserNotFoundException, LoginFailedException, BadRequestException {
		User userLogged = userService.verifyLogin(credentials);
		checkRequestCorrectness(id, userLogged);
		userService.deleteUserById(id);
		response.setStatus(HttpStatus.NO_CONTENT.value());
	}

	@GetMapping(path = "/id")
	public User getUserByIdWithNoId() throws BadRequestException {
		throw new BadRequestException();
	}

	@GetMapping(path = "/username")
	public User getUserByUsernameWithNoUsername() throws BadRequestException {
		throw new BadRequestException();
	}

	@GetMapping(path = "/usernamelike")
	public List<User> getUsersByUsernameLikeWithNoUsername() throws BadRequestException {
		throw new BadRequestException();
	}

	@PutMapping(path = "/update")
	public User updateUserWithNoId() throws BadRequestException {
		throw new BadRequestException();
	}

	@DeleteMapping(path = "/delete")
	public void deleteUserWithNoId() throws BadRequestException {
		throw new BadRequestException();
	}

	private void checkRequestCorrectness(Long id, User userLogged) throws BadRequestException {
		if (userLogged.getId() != id)
			throw new BadRequestException();
	}

}
