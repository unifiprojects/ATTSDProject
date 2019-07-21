package com.maurosalani.project.attsd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;
import com.maurosalani.project.attsd.repository.UserRepository;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(UserService.class)
@ActiveProfiles("mysql")
public class UserServiceRepositoryIT {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Test
	public void testServiceCanInsertIntoRepository() throws Exception {
		User saved = userService.insertNewUser(new User(null, "user", "password"));
		assertTrue(userRepository.findById(saved.getId()).isPresent());
	}

	@Test
	public void testServiceCanDeleteFromRepository() throws Exception {
		User saved = userService.insertNewUser(new User(null, "user", "password"));
		assertTrue(userRepository.findById(saved.getId()).isPresent());
		userService.deleteById(saved.getId());
		assertFalse(userRepository.findById(saved.getId()).isPresent());
	}

	@Test
	public void testServiceCanUpdateIntoRepository() throws Exception {
		User userToUpdate = new User(null, "username", "password");
		userRepository.save(userToUpdate);
		User userResulted = userService.updateUserById(userToUpdate.getId(),
				new User(userToUpdate.getId(), "newUsername", "newPassword"));

		assertThat(userRepository.findById(userToUpdate.getId()).get()).isEqualTo(userResulted);
	}

	@Test
	public void testServiceCanAddFollowedIntoRepository() throws Exception {
		User userToUpdate = new User(null, "username", "password");
		User followedToAdd = new User(null, "followed", "password");
		userRepository.save(userToUpdate);
		userRepository.save(followedToAdd);
		User userResulted = userService.addFollowedUser(userToUpdate, followedToAdd);

		assertThat(userRepository.findById(userToUpdate.getId()).get()).isEqualTo(userResulted);
	}

	@Test
	public void testServiceCanAddGameIntoRepository() throws Exception {
		User userToUpdate = new User(null, "username", "password");
		Game gameToAdd = new Game(null, "name", "description", new Date(1));
		userRepository.save(userToUpdate);
		gameRepository.save(gameToAdd);
		User userResulted = userService.addGame(userToUpdate, gameToAdd);

		assertThat(userRepository.findById(userToUpdate.getId()).get()).isEqualTo(userResulted);
	}

}
