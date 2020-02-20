package com.maurosalani.project.attsd.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Length(max = 40)
	@Column(unique = true)
	@Basic(optional = false)
	private String username;

	@Basic(optional = false)
	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "followers_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "followed_id"))
	@JsonIgnoreProperties({ "followedUsers", "followerUsers" })
	private List<User> followedUsers;

	@ManyToMany(mappedBy = "followedUsers", fetch = FetchType.LAZY)
	@JsonIgnoreProperties({ "followedUsers", "followerUsers" })
	private List<User> followerUsers;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_game_relation", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
	@JsonIgnoreProperties("users")
	private List<Game> games;

	public User() {

	}

	public User(Long id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}

	@PreRemove
	private void removeGamesForeignKeys() {
		for (User user : this.followedUsers) {
			user.getFollowedUsers().remove(this);
			user.getFollowerUsers().remove(this);
		}
		for (User user : this.followerUsers) {
			user.getFollowedUsers().remove(this);
			user.getFollowerUsers().remove(this);
		}
		for (Game game : this.games) {
			game.getUsers().remove(this);
		}
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
