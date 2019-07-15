package com.maurosalani.project.attsd.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;
	private String password;

	List<User> followedUsers;
	List<User> followerUsers;
	List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<User> getFollowedUsers() {
		return followedUsers;
	}

	public List<User> getFollowerUsers() {
		return followerUsers;
	}

	public List<Game> getGames() {
		return games;
	}

	public void addFollowedUser(User user) {
		if (this.followedUsers == null)
			this.followedUsers = new LinkedList<>();
		this.followedUsers.add(user);
	}

	public void addFollowerUser(User user) {
		if (this.followerUsers == null)
			this.followerUsers = new LinkedList<>();
		this.followerUsers.add(user);
	}

	public void addGame(Game game) {
		if (this.games == null)
			this.games = new LinkedList<>();
		this.games.add(game);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
