package com.maurosalani.project.attsd.model;

import java.util.List;
import java.util.Objects;

public class User {

	private Long id;

	private String username;
	private String password;

	List<User> followed;

	public User() {

	}

	public User(Long id, String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(Long id, String username, String password, List<User> followed) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.followed = followed;
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

	public List<User> getFollowed() {
		return followed;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFollowed(List<User> followed) {
		this.followed = followed;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User object = (User) obj;
		return Objects.equals(id, object.id) && Objects.equals(username, object.username)
				&& Objects.equals(password, object.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + "]";
	}

}
