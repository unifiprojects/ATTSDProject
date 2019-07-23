package com.maurosalani.project.attsd.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.Test;

public class UserTest {

	@Test
	public void testAddFollowedUser() {
		User user = new User(null, "user", "pwd");
		User followedUser = new User(null, "followed", "pwd_followed");
		user.addFollowedUser(followedUser);
		assertThat(user.getFollowedUsers()).containsExactly(followedUser);
	}
	
	@Test
	public void testAddFollowedUserWithArgumentNull() {
		User user = new User(null, "user", "pwd");
		user.addFollowedUser(null);
		assertThat(user.getFollowedUsers()).isNull();
	}

	@Test
	public void testAddFollowerUser() {
		User user = new User(null, "user", "pwd");
		User followerUser = new User(null, "follower", "pwd_follower");
		user.addFollowerUser(followerUser);
		assertThat(user.getFollowerUsers()).containsExactly(followerUser);
	}
	
	@Test
	public void testAddFollowerUserWithArgumentNull() {
		User user = new User(null, "user", "pwd");
		user.addFollowerUser(null);
		assertThat(user.getFollowerUsers()).isNull();
	}

	@Test
	public void testAddGame() {
		User user = new User(null, "user", "pwd");
		Game game = new Game(null, "game_name", "game_description", new Date(0));
		user.addGame(game);
		assertThat(user.getGames()).containsExactly(game);
	}
	
	@Test
	public void testAddGameWithArgumentNull() {
		User user = new User(null, "user", "pwd");
		user.addGame(null);
		assertThat(user.getGames()).isNull();
	}
	
	
	
}
