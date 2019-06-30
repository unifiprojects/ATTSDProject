package com.maurosalani.project.attsd.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String username;
	private String password;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "followers_relation")
	@JoinColumn(name = "follower_id", referencedColumnName = "id")
	@JoinColumn(name = "followed_id", referencedColumnName = "id")
	private List<User> followedUsers;

	@ManyToMany(mappedBy = "followedUsers")
	private List<User> followerUsers;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password, List<User> followedUsers, List<User> followerUsers,
			List<Game> games) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.followedUsers = followedUsers;
		this.followerUsers = followerUsers;
		this.games = games;
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

	public void setFollowedUsers(List<User> followedUsers) {
		this.followedUsers = followedUsers;
	}

	public List<User> getFollowerUsers() {
		return followerUsers;
	}

	public void setFollowerUsers(List<User> followerUsers) {
		this.followerUsers = followerUsers;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
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
				&& Objects.equals(followerUsers, object.followerUsers) && Objects.equals(games, object.games);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username, password, followedUsers, followerUsers, games);
	}

	@Override
	public String toString() {
		String followedUserString;
		String followerUserString;
		String gamesString;
		if (followedUsers != null) {
			followedUserString = followedUsers.toString();
		} else {
			followedUserString = "None";
		}
		if (followerUsers != null) {
			followerUserString = followerUsers.toString();
		} else {
			followerUserString = "None";
		}
		if (games != null) {
			gamesString = games.toString();
		} else {
			gamesString = "None";
		}
		return "User [id=" + id + ", username=" + username + ", followed=" + followedUserString + ", follower="
				+ followerUserString + ", games=" + gamesString + "]";
	}

}
