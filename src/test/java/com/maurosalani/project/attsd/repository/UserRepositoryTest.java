package com.maurosalani.project.attsd.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static java.util.Arrays.asList;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("h2")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Before
	public void clearDatabase() {
		repository.deleteAll();
		repository.flush();
	}

	@Test
	public void testFindAllWithEmptyDatabase() {
		List<User> users = repository.findAll();
		assertThat(users).isEmpty();
	}

	@Test
	public void testFindAllWithExistingUser() {
		User user = new User(null, "test", "pwd");
		User saved = entityManager.persistFlushFind(user);
		List<User> users = repository.findAll();

		assertThat(users).containsExactly(saved);
	}

	@Test
	public void testFindByUsername() {
		User user = new User(null, "test", "pwd");
		User saved = entityManager.persistFlushFind(user);
		Optional<User> userFound = repository.findByUsername("test");

		assertThat(userFound.get()).isEqualTo(saved);
	}

	@Test
	public void testFindByUsernameAndPassword() {
		User user = new User(null, "test", "pwd");
		User saved = entityManager.persistFlushFind(user);
		Optional<User> userFound = repository.findByUsernameAndPassword("test", "pwd");

		assertThat(userFound.get()).isEqualTo(saved);
	}

	@Test
	public void testFindByUsernameLike() {
		User user1 = new User(null, "user one", "pwd");
		User user2 = new User(null, "user two", "pwd");
		User saved1 = entityManager.persistFlushFind(user1);
		User saved2 = entityManager.persistFlushFind(user2);

		List<User> usersFound = repository.findByUsernameLike("%user%");

		assertThat(usersFound).containsExactlyInAnyOrder(saved1, saved2);
	}

	@Test
	public void testUsernameAndPasswordAreMandatoryWhenUserIsSaved() {
		User userNoUsername = new User(null, null, "pwd");
		User userNoPassword = new User(null, "username", null);
		User userNoUsernameAndPassword = new User(null, null, "pwd");

		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsername));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoPassword));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsernameAndPassword));
	}

	@Test
	public void testFollowedListIsRetrievableWhenUserIsSaved() {
		User followed1 = new User(null, "one", "pwd");
		User followed2 = new User(null, "two", "pwd");
		User user = new User(null, "test", "pwd");

		User followed1Persisted = entityManager.persistFlushFind(followed1);
		User followed2Persisted = entityManager.persistFlushFind(followed2);
		user.addFollowedUser(followed1Persisted);
		user.addFollowedUser(followed2Persisted);
		User saved = entityManager.persistFlushFind(user);

		assertThat(asList(followed1Persisted, followed2Persisted)).isEqualTo(saved.getFollowedUsers());
		assertThat(followed1Persisted.getFollowerUsers().get(0)).isEqualTo(saved);
		assertThat(followed2Persisted.getFollowerUsers().get(0)).isEqualTo(saved);
	}

	@Test
	public void testFollowerListIsRetrievableWhenUserIsSaved() {
		User follower1 = new User(null, "one", "pwd");
		User follower2 = new User(null, "two", "pwd");
		User user = new User(null, "test", "pwd");
		
		User saved = entityManager.persistFlushFind(user);
		follower1.addFollowedUser(saved);
		follower2.addFollowedUser(saved);
		User follower1Persisted = entityManager.persistFlushFind(follower1);
		User follower2Persisted = entityManager.persistFlushFind(follower2);
		
		saved = entityManager.find(User.class, saved.getId());
		assertThat(saved.getFollowerUsers().get(0)).isIn(follower1Persisted, follower2Persisted);
		assertThat(saved.getFollowerUsers().get(1)).isIn(follower1Persisted, follower2Persisted);
		assertThat(follower1Persisted.getFollowedUsers().get(0)).isEqualTo(saved);
		assertThat(follower2Persisted.getFollowedUsers().get(0)).isEqualTo(saved);
	}

	@Test
	public void testGamesListIsRetrievableWhenUserIsSaved() {
		Game game1 = new Game(null, "game1", "description1", new Date(0));
		Game game2 = new Game(null, "game2", "description2", new Date(0));
		User user = new User(null, "test", "pwd");

		Game game1Persisted = entityManager.persistFlushFind(game1);
		Game game2Persisted = entityManager.persistFlushFind(game2);
		user.addGame(game1Persisted);
		user.addGame(game2Persisted);
		User saved = entityManager.persistFlushFind(user);

		assertThat(asList(game1Persisted, game2Persisted)).isEqualTo(saved.getGames());
		assertThat(game1Persisted.getUsers().get(0)).isEqualTo(saved);
		assertThat(game2Persisted.getUsers().get(0)).isEqualTo(saved);
	}

	@Test
	public void testFollowedUsersAreUpdatedWithExistingUser() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "followed_one", "pwd_one"));
		followed.add(new User(null, "followed_two", "pwd_two"));
		User user = new User(null, "user", "pwd");
		user.setFollowedUsers(followed);

		entityManager.persistAndFlush(followed.get(0));
		entityManager.persistAndFlush(followed.get(1));
		User savedAndToBeUpdated = entityManager.persistFlushFind(user);

		List<User> replacementFollowedUsers = new LinkedList<User>();
		replacementFollowedUsers.add(new User(null, "followed_one_replacement", "pwd_one_replacement"));
		replacementFollowedUsers.add(new User(null, "followed_two_replacement", "pwd_two_replacement"));
		savedAndToBeUpdated.setFollowedUsers(replacementFollowedUsers);

		User updated = repository.save(savedAndToBeUpdated);

		assertThat(replacementFollowedUsers).isEqualTo(updated.getFollowedUsers());
	}

	@Test
	public void testUsernameIsUpdated() {
		User user = new User(null, "username", "pwd");
		User savedAndToUpdate = entityManager.persistFlushFind(user);
		savedAndToUpdate.setUsername("new_username");

		User updated = repository.save(savedAndToUpdate);

		assertThat(updated.getUsername()).isEqualTo("new_username");
	}

	@Test
	public void testUsernameIsNotUpdatedIfAlreadyExists_ShouldThrowException() {
		User user1 = new User(null, "username1", "pwd1");
		User user2 = new User(null, "username2", "pwd2");
		entityManager.persistFlushFind(user1);
		User saved2_toUpdate = entityManager.persistFlushFind(user2);

		saved2_toUpdate.setUsername("username1");
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(saved2_toUpdate));
	}

	@Test
	public void testDeleteByIdOfSavedUser() {
		User user = new User(null, "test", "pwd");
		User saved = entityManager.persistFlushFind(user);
		repository.deleteById(saved.getId());
		User found = entityManager.find(User.class, saved.getId());

		assertThat(found).isEqualTo(null);
	}

	@Test
	public void testFindGamesOfUserByUsername() {
		List<Game> games = new LinkedList<Game>();
		games.add(new Game(null, "game1", "description1", new Date(0)));
		games.add(new Game(null, "game2", "description2", new Date(0)));

		User user = new User(null, "username_test", "pwd");
		user.setGames(games);
		entityManager.persistAndFlush(games.get(0));
		entityManager.persistAndFlush(games.get(1));
		User saved = entityManager.persistFlushFind(user);

		List<Game> retrievedGames = repository.findGamesOfUserByUsername("username_test");
		assertThat(retrievedGames).isEqualTo(saved.getGames());
	}

	@Test
	public void testFindFollowedOfUserByUsername() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "one", "pwd"));
		followed.add(new User(null, "two", "pwd"));
		User user = new User(null, "test", "pwd");
		user.setFollowedUsers(followed);

		entityManager.persistAndFlush(followed.get(0));
		entityManager.persistAndFlush(followed.get(1));
		User saved = entityManager.persistFlushFind(user);
		List<User> found = repository.findFollowedOfUserByUsername("test");

		assertThat(found).isEqualTo(saved.getFollowedUsers());
	}

	@Test
	public void testFindFollowerOfUserByUsername() {
		List<User> follower = new LinkedList<User>();
		follower.add(new User(null, "one", "pwd"));
		follower.add(new User(null, "two", "pwd"));
		User user = new User(null, "test", "pwd");
		user.setFollowerUsers(follower);

		follower.stream().forEach(u -> u.addFollowedUser(user));

		User saved = entityManager.persistFlushFind(user);
		List<User> found = repository.findFollowerOfUserByUsername("test");

		assertThat(found).isEqualTo(saved.getFollowerUsers());
	}
}
