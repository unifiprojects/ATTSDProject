package com.maurosalani.project.attsd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	private UserService userService;

	@GetMapping("/api/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

}
