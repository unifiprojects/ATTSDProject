package com.maurosalani.project.attsd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.exception.BadRequestException;
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

	@GetMapping(path = "/id", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserByIdWithNoId() throws BadRequestException, UserNotFoundException {
		User user;
		try {
			user = userService.getUserById(null);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException();
		}
		return user;
	}

	@GetMapping(path = "/username/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User getUserByUsername(@PathVariable String username) throws UserNotFoundException {
		return userService.getUserByUsername(username);
	}

	@GetMapping(path = "/usernamelike/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<User> getUsersByUsernameLike(@PathVariable String username) {
		return userService.getUsersByUsernameLike(username);
	}

}
