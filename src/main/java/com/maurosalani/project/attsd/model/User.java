package com.maurosalani.project.attsd.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	private String username;
	private String password;

	@OneToMany
	@JoinTable(name = "followers")
	@JoinColumn(name = "person_id", referencedColumnName = "id")
	@JoinColumn(name = "followed_id", referencedColumnName = "id")
	private List<User> followedUsers;

	@OneToMany
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password, List<User> followedUsers, List<Game> games) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.followedUsers = followedUsers;
		this.games = games;
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
		return followedUsers;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFollowed(List<User> followed) {
		this.followedUsers = followed;
	}

	public void addFollowed(User user) {
		followedUsers.add(user);
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public void addGame(Game game) {
		games.add(game);
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
				&& Objects.equals(password, object.password) && Objects.equals(followedUsers, object.followedUsers)
				&& Objects.equals(games, object.games);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, followedUsers, games);
	}

	@Override
	public String toString() {
		String followedUserString;
		String gamesString;
		if (followedUsers != null) {
			followedUserString = followedUsers.toString();
		} else {
			followedUserString = "None";
		}
		if (games != null) {
			gamesString = games.toString();
		} else {
			gamesString = "None";
		}
		return "User [id=" + id + ", username=" + username + ", followed=" + followedUserString + ", games="
				+ gamesString + "]";
	}

}
