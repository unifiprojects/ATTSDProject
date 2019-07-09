package com.maurosalani.project.attsd.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;

@DataJpaTest
@RunWith(SpringRunner.class)
public class GameRepositoryTest {

	@Autowired
	private GameRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testFindAllWithEmptyDatabase() {
		List<Game> users = repository.findAll();
		assertThat(users).isEmpty();
	}

	@Test
	public void testFindAllWithExistingGame() {
		Game game = new Game(null, "game name", "game description", new Date(1000));
		Game saved = entityManager.persistFlushFind(game);

		List<Game> games = repository.findAll();

		assertThat(games).containsExactly(saved);
	}

	@Test
	public void testFindByName() {
		Game game = new Game(null, "game name", "game description", new Date(1000));
		Game saved = entityManager.persistFlushFind(game);

		Game gameFound = repository.findByName("game name");

		assertThat(gameFound).isEqualTo(saved);
	}

	@Test
	public void testFindByNameLike() {
		Game game1 = new Game(null, "game name 1", "game description 1", new Date(1000));
		Game game2 = new Game(null, "game name 2", "game description 2", new Date(1000));
		Game saved1 = entityManager.persistFlushFind(game1);
		Game saved2 = entityManager.persistFlushFind(game2);

		List<Game> gamesFound = repository.findByNameLike("%game%");

		assertThat(gamesFound).containsExactlyInAnyOrder(saved1, saved2);
	}
	
	@Test
	public void testNameIsMandatoryWhenGameIsSaved() {
		Game gameNoName = new Game(null, null, "game_description", new Date(1000));
		
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(gameNoName));
	}

	@Test
	public void testUsersListIsPersistedWhenGameIsSaved() {
		List<User> users = new LinkedList<User>();
		users.add(new User(null, "one", "pwd"));
		users.add(new User(null, "two", "pwd"));
		Game game = new Game(null, "game name", "game description", new Date(1000));
		game.setUsers(users);

		Game saved = repository.save(game);

		assertThat(users).isEqualTo(saved.getUsers());
	}

	@Test
	public void testUsersListIsUpdatedWithExistingUser() {
		List<User> users = new LinkedList<User>();
		users.add(new User(null, "user_one", "pwd_one"));
		users.add(new User(null, "user_two", "pwd_two"));
		Game game = new Game(null, "game name", "game description", new Date(1000));
		game.setUsers(users);

		Game savedAndToBeUpdated = entityManager.persistFlushFind(game);

		List<User> usersReplacement = new LinkedList<User>();
		usersReplacement.add(new User(null, "user_one_replacement", "pwd_one_replacement"));
		usersReplacement.add(new User(null, "user_two_replacement", "pwd_two_replacement"));
		savedAndToBeUpdated.setUsers(usersReplacement);

		Game updated = repository.save(savedAndToBeUpdated);

		assertThat(usersReplacement).isEqualTo(updated.getUsers());
	}

	@Test
	public void testNameIsUpdated() {
		Game game = new Game(null, "game_name", "game_description", new Date());
		Game savedAndToUpdate = entityManager.persistFlushFind(game);
		savedAndToUpdate.setName("new_name");

		Game updated = repository.save(savedAndToUpdate);

		assertThat(updated.getName()).isEqualTo("new_name");
	}

	@Test
	public void testDescriptionIsUpdated() {
		Game game = new Game(null, "game_name", "game_description", new Date());
		Game savedAndToUpdate = entityManager.persistFlushFind(game);
		savedAndToUpdate.setDescription("new_description");

		Game updated = repository.save(savedAndToUpdate);

		assertThat(updated.getDescription()).isEqualTo("new_description");
	}

	@Test
	public void testDeleteByIdOfSaveGame() {
		Game game = new Game(null, "game name", "game description", new Date());
		Game saved = entityManager.persistFlushFind(game);

		repository.deleteById(saved.getId());
		Game gameFound = entityManager.find(Game.class, saved.getId());

		assertThat(gameFound).isEqualTo(null);
	}

	@Test
	public void testFindUsersListOfGameByUsername() {
		List<User> users = new LinkedList<User>();
		users.add(new User(null, "one", "pwd"));
		users.add(new User(null, "two", "pwd"));

		Game game = new Game(null, "game name", "game description", new Date(1000));
		game.setUsers(users);
		users.stream().forEach(user -> user.addGame(game));

		Game saved = entityManager.persistFlushFind(game);

		List<User> retrievedUsers = repository.findUsersOfGameByName("game name");
		assertThat(retrievedUsers).isEqualTo(saved.getUsers());
	}

}
