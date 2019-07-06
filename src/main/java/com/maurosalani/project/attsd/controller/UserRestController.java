package com.maurosalani.project.attsd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	private UserService userService;

	@GetMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

}
