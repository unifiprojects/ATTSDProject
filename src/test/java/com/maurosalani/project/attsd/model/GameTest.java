package com.maurosalani.project.attsd.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;

public class GameTest {

	@Test
	public void testAddUser() {
		Game game = new Game(null, "game_name", "game_description", new Date());
		User user = new User(null, "user", "pwd_user");
		game.addUsers(user);
		assertThat(game.getUsers()).containsExactly(user);
	}
	
	@Test
	public void testAddUserWithArgumentNull() {
		Game game = new Game(null, "game_name", "game_description", new Date());
		game.addUsers(null);
		assertThat(game.getUsers()).isNull();
	}

}
