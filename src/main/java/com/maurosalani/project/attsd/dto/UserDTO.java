package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.User;

public class UserDTO {

	private Long id;

	private String username;

	private String password;

	public UserDTO() {
	}

	public UserDTO(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public User getUser() {
		return new User(this.id, this.username, this.password);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
