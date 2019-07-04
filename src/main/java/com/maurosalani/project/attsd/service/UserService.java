package com.maurosalani.project.attsd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return userRepository.findAllUsers();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public User insertNewUser(User user) {
		if (user == null)
			return null;

		user.setId(null);
		return userRepository.save(user);
	}

	public User updateUserById(Long id, User user) {
		if (user == null)
			return null;

		user.setId(id);
		return userRepository.save(user);
	}

}
