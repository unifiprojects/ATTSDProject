package com.maurosalani.project.attsd.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.Length;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Length(max = 40)
	@Column(unique = true)
	@Basic(optional = false)
	private String username;

	@Basic(optional = false)
	private String password;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "followers_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
	private List<User> followedUsers;

	@ManyToMany(mappedBy = "followedUsers", cascade = CascadeType.ALL)
	private List<User> followerUsers;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_game_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public User(Long id, String username, String password, List<User> followedUsers, List<User> followerUsers,
			List<Game> games) {
		super();
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

	public void addFollowedUser(User followedUser) {
		if (followedUser != null) {
			if (this.followedUsers == null)
				this.followedUsers = new LinkedList<>();
			this.followedUsers.add(followedUser);
		}
	}

	public void addFollowerUser(User followerUser) {
		if (followerUser != null) {
			if (this.followerUsers == null)
				this.followerUsers = new LinkedList<>();
			this.followerUsers.add(followerUser);
		}
	}

	public void addGame(Game game) {
		if (game != null) {
			if (this.games == null)
				this.games = new LinkedList<>();
			this.games.add(game);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((followedUsers == null) ? 0 : followedUsers.hashCode());
		result = prime * result + ((followerUsers == null) ? 0 : followerUsers.hashCode());
		result = prime * result + ((games == null) ? 0 : games.hashCode());
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
		if (followedUsers == null) {
			if (other.followedUsers != null)
				return false;
		}
		else if (!followedUsers.equals(other.followedUsers))
			return false;
		if (followerUsers == null) {
			if (other.followerUsers != null)
				return false;
		}
		else if (!followerUsers.equals(other.followerUsers))
			return false;
		if (games == null) {
			if (other.games != null)
				return false;
		}
		else if (!games.equals(other.games))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} 
		else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} 
		else if (!username.equals(other.username))
			return false;
		return true;
	}
}
