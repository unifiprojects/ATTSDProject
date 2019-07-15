package com.maurosalani.project.attsd.controller;

import com.maurosalani.project.attsd.model.User;

public class UpdateUserForm {

	private String username;
	private String password;
	private User userToUpdate;

	public UpdateUserForm() {
	};

	public UpdateUserForm(String username, String password, User userToUpdate) {
		super();
		this.username = username;
		this.password = password;
		this.userToUpdate = userToUpdate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUserToUpdate() {
		return userToUpdate;
	}

	public void setUserToUpdate(User userToUpdate) {
		this.userToUpdate = userToUpdate;
	}

}
