package com.maurosalani.project.attsd.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.Test;

public class GameTest {

	@Test
	public void testAddUser() {
		Game game = new Game(null, "game_name", "game_description", new Date(0));
		User user = new User(null, "user", "pwd_user");
		game.addUser(user);
		assertThat(game.getUsers()).containsExactly(user);
	}
	
	@Test
	public void testAddUserWithArgumentNull() {
		Game game = new Game(null, "game_name", "game_description", new Date(0));
		game.addUser(null);
		assertThat(game.getUsers()).isNull();
	}

}
